package com.wjl.gmall.order.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wjl.gmall.cart.client.CartServiceClient;
import com.wjl.gmall.cart.model.dto.CartInfo;
import com.wjl.gmall.model.enums.OrderStatus;
import com.wjl.gmall.model.enums.PaymentWay;
import com.wjl.gmall.model.enums.ProcessStatus;
import com.wjl.gmall.order.mapper.OrderDetailMapper;
import com.wjl.gmall.order.mapper.OrderInfoMapper;
import com.wjl.gmall.order.model.entity.OrderDetail;
import com.wjl.gmall.order.model.entity.OrderInfo;
import com.wjl.gmall.order.service.OrderService;
import com.wjl.gmall.product.client.ProductServiceClient;
import com.wjl.gmall.user.client.UserServiceClient;
import com.wjl.gmall.user.dto.UserAddress;
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

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/28
 * @description
 */
@Service
public class OrderServiceImpl implements OrderService {

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
    public Long submitOrder(OrderInfo orderInfo, String userId) {
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
            // 获取最新的价格 优化，整一个list skuId过去 返回一个map<skuId,Price>
            item.setOrderPrice(productServiceClient.getSkuPrice(item.getSkuId()).getData());
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
        return orderInfo.getId();
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
}
