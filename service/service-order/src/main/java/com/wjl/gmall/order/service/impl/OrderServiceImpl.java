package com.wjl.gmall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.gmall.cart.client.CartServiceClient;
import com.wjl.gmall.cart.model.dto.CartInfo;
import com.wjl.gmall.common.execption.BusinessException;
import com.wjl.gmall.common.service.RabbitService;
import com.wjl.gmall.model.enums.OrderStatus;
import com.wjl.gmall.model.enums.PaymentType;
import com.wjl.gmall.model.enums.PaymentWay;
import com.wjl.gmall.model.enums.ProcessStatus;
import com.wjl.gmall.order.config.OrderCancelMqConfig;
import com.wjl.gmall.order.mapper.OrderDetailMapper;
import com.wjl.gmall.order.mapper.OrderInfoMapper;
import com.wjl.gmall.order.model.entity.OrderDetail;
import com.wjl.gmall.order.model.entity.OrderInfo;
import com.wjl.gmall.order.model.vo.OrderWareVO;
import com.wjl.gmall.order.model.vo.WareSkuVo;
import com.wjl.gmall.order.service.OrderService;
import com.wjl.gmall.product.client.ProductServiceClient;
import com.wjl.gmall.user.client.UserServiceClient;
import com.wjl.gmall.user.dto.UserAddress;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.wjl.gmall.common.constants.MqConst.*;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/28
 * @description
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderService {

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private CartServiceClient cartServiceClient;

    @Autowired
    private ProductServiceClient productServiceClient;

    @Autowired
    private ThreadPoolExecutor poolExecutor;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RabbitService rabbitService;


    @Value("${ware.url}")
    private String wareUrl;

    /**
     * 获取结算确认页的数据
     *
     * @param userId
     * @return
     */
    @Override
    public Map<String, Object> trade(String userId) {
        long id = Long.parseLong(userId);
        Map<String, Object> result = new HashMap<>();

        CompletableFuture<Void> userAddressListFuture = CompletableFuture.runAsync(() -> {
            // 地址列表
            List<UserAddress> userAddressListByUserId = userServiceClient.findUserAddressListByUserId(id);
            result.put("userAddressList", userAddressListByUserId);
            // 流水号
            result.put("tradeNo", getTradeNoCode(userId));
        }, poolExecutor);
        CompletableFuture<Void> detailArraysListFuture = CompletableFuture.runAsync(() -> {
            // 数量
            AtomicInteger totalNum = new AtomicInteger(0);
            // 总金额
            BigDecimal totalAmount = null;
            // 封装购物列表
            List<CartInfo> cartCheckedList = cartServiceClient.getCartCheckedList(id);
            List<OrderDetail> detailArrayList = cartCheckedList.stream().map(cartInfo -> {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setSkuId(cartInfo.getSkuId());
                orderDetail.setSkuName(cartInfo.getSkuName());
                Integer skuNum = cartInfo.getSkuNum();
                totalNum.addAndGet(skuNum);
                orderDetail.setSkuNum(skuNum);
                // 查询实时价格
                BigDecimal price = productServiceClient.getSkuPrice(cartInfo.getSkuId()).getData();
                orderDetail.setOrderPrice(price);
                orderDetail.setImgUrl(cartInfo.getImgUrl());
                orderDetail.setCreateTime(new Date());
                orderDetail.setUpdateTime(new Date());
                return orderDetail;
            }).collect(Collectors.toList());
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setOrderDetailList(detailArrayList);
            orderInfo.sumTotalAmount(); // 计算商品总价格
            totalAmount = orderInfo.getTotalAmount();
            result.put("totalAmount", totalAmount);
            result.put("detailArrayList", detailArrayList);
            result.put("totalNum", totalNum);
        }, poolExecutor);

        CompletableFuture.allOf(detailArraysListFuture, userAddressListFuture).join();
        return result;
    }

    /**
     * 提交订单
     * 返回订单id
     * 数据校验
     * 防止重复提交
     * 删除相关购物车数据
     *
     * @param orderInfo
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long submitOrder(OrderInfo orderInfo, String userId, boolean isActivity) {
        // 用户id
        orderInfo.setUserId(Long.parseLong(userId));
        // 收货人
        // 手机号
        // 支付状态
        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());
        // 付款方式
        orderInfo.setPaymentWay(PaymentWay.ONLINE.getComment());
        // 交易订单号
        String outTradeNo = "wjl" + UUID.randomUUID().toString().replace("-", "");
        orderInfo.setOutTradeNo(outTradeNo);
        // 订单描述
        StringBuilder tradeBody = new StringBuilder();
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        orderDetailList.forEach(item -> {
            tradeBody.append("skuName:").append(item.getSkuName())
                    .append(" num:").append(item.getSkuNum())
                    .append(",");
            if (!isActivity) {
                // 获取最新的价格 优化，整一个list skuId过去 返回一个map<skuId,Price>
                item.setOrderPrice(productServiceClient.getSkuPrice(item.getSkuId()).getData());
            }
        });
        orderInfo.sumTotalAmount();// 计算价格
        String tradeBodyString = tradeBody.toString().length() > 100 ? tradeBody.toString().substring(0, 100) : tradeBody.toString();
        orderInfo.setTradeBody(tradeBodyString);
        // 操作时间
        orderInfo.setOperateTime(new Date());
        // 失效时间 一天后
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE, 1);
        orderInfo.setExpireTime(instance.getTime());
        // 进度状态
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());
        // TODO 物流，优惠等等

        // 保存订单
        orderInfoMapper.insert(orderInfo);

        ArrayList<Long> skuIds = new ArrayList<>();

        // 保存订单详情
        orderDetailList.forEach(item -> {
            item.setOrderId(orderInfo.getId()); // 订单id
            // 验证金额是否合法
            skuIds.add(item.getSkuId());
            // 保存
            orderDetailMapper.insert(item);
        });

        // 删除购物车相关数据
        Long orderInfoId = orderInfo.getId();
        // 发送延时消息监控订单状态
        rabbitService.sendMessage(OrderCancelMqConfig.ORDER_CANCEL_EXCHANGE, OrderCancelMqConfig.ORDER_CANCEL_KEY, orderInfoId);
        return orderInfoId;
    }

    @Override
    public String getTradeNoCode(String userId) {
        String tradeNoCode = UUID.randomUUID().toString().replace("-", "");
        String key = "user:" + userId + ":tradeNo";
        redisTemplate.opsForValue().set(key, tradeNoCode);
        return tradeNoCode;
    }

    @Override
    public boolean checkTradeNoCode(String userId, String tradeNo) {
        String key = "user:" + userId + ":tradeNo";
        String code = redisTemplate.opsForValue().get(key);
        if (StringUtils.hasText(code)) {
            return code.equals(tradeNo);
        }
        return false;
    }

    @Override
    public void deleteTradeNoCode(String userId) {
        String key = "user:" + userId + ":tradeNo";
        redisTemplate.delete(key);
    }

    @Override
    public boolean checkStock(String skuId, String num) {
        // http://localhost:9001/hasStock?skuId=22&num=200
        String template = wareUrl + "/hasStock?skuId=%s&num=%s";
        String result = restTemplate.getForObject(String.format(template, skuId, num), String.class);
        return "1".equals(result);
    }

    @Override
    public boolean checkedPrice(OrderInfo orderInfo, String userId) {
        HashMap<Long, BigDecimal> map = new HashMap<>();
        List<CompletableFuture<Void>> tasks = new ArrayList<>();
        for (OrderDetail orderDetail : orderInfo.getOrderDetailList()) {
            CompletableFuture<Void> getPriceTask = CompletableFuture.runAsync(() -> {
                BigDecimal newPrice = productServiceClient.getSkuPrice(orderDetail.getSkuId()).getData();
                if (orderDetail.getOrderPrice().compareTo(newPrice) != 0) {
                    map.put(orderDetail.getSkuId(), newPrice);
                }
            }, poolExecutor);
            tasks.add(getPriceTask);
        }

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()])).join();
        if (!CollectionUtils.isEmpty(map)) {
            // 通知更新缓存中的价格
            cartServiceClient.updateCartCache(map, userId);
            return true;
        }
        return false;
    }

    @Override
    public IPage<OrderInfo> getOrders(String userId, Long page, Long limit) {
        IPage<OrderInfo> ordersPage = orderInfoMapper.getOrdersPage(new Page<>(page, limit), userId);
        for (OrderInfo record : ordersPage.getRecords()) {
            record.setOrderStatusName(OrderStatus.getStatusNameByStatus(record.getOrderStatus()));
        }
        return ordersPage;
    }

    @Override
    public void execExpireOrder(Long orderInfoId, String flag, PaymentType type) {
        this.updateOrderProcessStatus(orderInfoId, ProcessStatus.CLOSED);
        if ("2".equals(flag)) {
            rabbitService.sendMessage(EXCHANGE_DIRECT_PAYMENT_CLOSE, ROUTING_PAYMENT_CLOSE, orderInfoId + "_" + type.name());
        }
    }

    @Transactional
    @Override
    public void updateOrderProcessStatus(Long orderInfoId, ProcessStatus status) {
        OrderInfo info = new OrderInfo();
        info.setId(orderInfoId);
        info.setOrderStatus(status.getOrderStatus().name());
        info.setProcessStatus(status.getOrderStatus().name());
        updateById(info);
    }

    @Override
    public OrderInfo getOrderInfo(Long orderId) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getId, orderId);
        OrderInfo info = this.getOne(queryWrapper);
        if (info != null) {
            LambdaQueryWrapper<OrderDetail> detailWrapper = new LambdaQueryWrapper<>();
            detailWrapper.eq(OrderDetail::getOrderId, orderId);
            List<OrderDetail> orderDetailList = orderDetailMapper.selectList(detailWrapper);
            info.setOrderDetailList(orderDetailList);
        }
        info.sumTotalAmount();
        return info;
    }

    @Override
    public OrderInfo getOrderInfoWithStatus(Long orderId, OrderStatus status) {
        LambdaQueryWrapper<OrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfo::getId, orderId).eq(OrderInfo::getOrderStatus, status.name()).eq(OrderInfo::getProcessStatus, status.name());
        OrderInfo info = this.getOne(queryWrapper);
        if (info != null) {
            LambdaQueryWrapper<OrderDetail> detailWrapper = new LambdaQueryWrapper<>();
            detailWrapper.eq(OrderDetail::getOrderId, orderId);
            List<OrderDetail> orderDetailList = orderDetailMapper.selectList(detailWrapper);
            info.setOrderDetailList(orderDetailList);
        }
        info.sumTotalAmount();
        return info;
    }

    /**
     * 发送订单扣减消息
     *
     * @param orderInfo
     */
    @Override
    public void sendOrderStatus(OrderInfo orderInfo) {
        // 查询订单
        this.updateOrderProcessStatus(orderInfo.getId(), ProcessStatus.NOTIFIED_WARE);
        // 封装数据对象
        String json = this.convertWareJson(orderInfo);
        // 发送消息
        rabbitService.sendMessage(EXCHANGE_DIRECT_WARE_STOCK, ROUTING_WARE_STOCK, json);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<OrderWareVO> orderSplit(String orderId, List<WareSkuVo> wareSkuVos) {
        ArrayList<OrderWareVO> orderWareVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(wareSkuVos)) {
            Long oId = Long.parseLong(orderId);
            // 获取父订单对象
            OrderInfo orderInfo = this.getOrderInfo(oId);
            List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
            // 拆分子订单
            Map<Long, OrderInfo> subOrderInfoMap = wareSkuVos.stream().collect(Collectors.toMap(k -> Long.parseLong(k.getWareId()), item -> {
                List<String> skuIds = item.getSkuIds();
                // 创建子订单
                OrderInfo subOrderInfo = new OrderInfo();
                BeanUtils.copyProperties(orderInfo, subOrderInfo);
                // 置空id
                subOrderInfo.setId(null);
                // 设置父id
                subOrderInfo.setParentOrderId(oId);
                // 所属当前仓库的
                StringBuilder desc = new StringBuilder();
                ArrayList<OrderDetail> subDetails = new ArrayList<>();
                if (!CollectionUtils.isEmpty(orderDetailList)) {
                    orderDetailList.forEach(skuInfo -> {
                        // 是当前仓库的商品
                        for (String skuId : skuIds) {
                            if (skuInfo.getSkuId().toString().equals(skuId)) {
                                subDetails.add(skuInfo);
                                desc.append("skuName:").append(skuInfo.getSkuName()).append(" ").append("num:").append(skuInfo.getSkuNum());
                            }
                        }
                    });
                }
                subOrderInfo.setOrderDetailList(subDetails);
                // 计算价格
                subOrderInfo.sumTotalAmount();
                // 描述
                subOrderInfo.setTradeBody(desc.toString());
                return subOrderInfo;
            }));

            // 保存子订单
            this.saveBatch(subOrderInfoMap.values());
            subOrderInfoMap.values().forEach(item -> {
                item.getOrderDetailList().forEach(skuInfo -> {
                    skuInfo.setOrderId(item.getId());
                    // 批量保存更好 懒
                    orderDetailMapper.insert(skuInfo);
                });
            });

            // 修改父订单状态
            this.updateOrderProcessStatus(orderInfo.getId(), ProcessStatus.SPLIT);

            subOrderInfoMap.forEach((wareId, subOrderInfo) -> {
                orderWareVOS.add(new OrderWareVO(subOrderInfo, wareId));
            });
        }
        return orderWareVOS;
    }

    @Override
    public Long submitOrder(OrderInfo orderInfo, String tradeNo, String userId, boolean isActivity) {
        // 验证流水号
        if (!this.checkTradeNoCode(userId, tradeNo)) {
            throw new BusinessException("不能重复提交订单!");
        }
        // 验证价格是否变动 不是优惠就验证价格
        if (!isActivity) {
            // 验证库存
            boolean flag = true;
            for (OrderDetail item : orderInfo.getOrderDetailList()) {
                flag = this.checkStock(item.getSkuId().toString(), item.getSkuNum().toString());
                if (!flag) {
                    throw new BusinessException(item.getSkuName() + "库存不足");
                }
            }
            boolean checked = this.checkedPrice(orderInfo, userId);
            if (checked) {
                throw new BusinessException("价格变更!!!");
            }
        } else {
            // TODO: 验证优惠价格
        }

        Long orderId = this.submitOrder(orderInfo, userId, isActivity);
        // 删除流水号
        this.deleteTradeNoCode(userId);
        return orderId;
    }

    public String convertWareJson(OrderInfo orderInfo) {
        orderInfo = this.getOrderInfo(orderInfo.getId());
        return new OrderWareVO(orderInfo, 1L).toJson();
    }


}
