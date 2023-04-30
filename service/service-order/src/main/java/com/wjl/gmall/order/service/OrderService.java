package com.wjl.gmall.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wjl.gmall.model.enums.OrderStatus;
import com.wjl.gmall.model.enums.ProcessStatus;
import com.wjl.gmall.order.model.entity.OrderInfo;

import java.util.Map;

/**
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/28
 * @description
 */
public interface OrderService extends IService<OrderInfo> {
    Map<String, Object> trade(String userId);

    /**
     * 提交订单
     *
     * @param orderInfo
     * @param userId
     * @return
     */
    Long submitOrder(OrderInfo orderInfo, String userId);

    /**
     * 根据用户生成相关的流水号
     *
     * @param userId
     * @return
     */
    String getTradeNoCode(String userId);

    /**
     * 校验流水号
     *
     * @param userId
     * @param TradeNo
     * @return
     */
    boolean checkTradeNoCode(String userId, String TradeNo);


    /**
     * 删除流水号
     *
     * @param userId
     */
    void deleteTradeNoCode(String userId);

    /**
     * 校验库存
     *
     * @param skuId
     * @param num
     * @return
     */
    boolean checkStock(String skuId, String num);

    /**
     * 验证接收的数据是否与实时的数据出现差异，出现则通知cart服务更新数据
     *
     * @param orderInfo
     * @return
     */
    boolean checkedPrice(OrderInfo orderInfo, String userId);

    /**
     * 获取订单数据
     *
     * @return
     */
    IPage<OrderInfo> getOrders(String userId, Long page, Long limit);

    /**
     * 处理超时订单
     *
     * @param orderInfoId
     */
    void execExpireOrder(Long orderInfoId);


    /**
     * 更新订单进度状态
     *
     * @param orderInfoId
     * @param status
     */
    void updateOrderProcessStatus(Long orderInfoId, ProcessStatus status);

    /**
     * 获取订单详情页数据
     *
     * @param orderId
     * @return
     */
    OrderInfo getOrderInfo(Long orderId);

    /**
     *  根据状态获取订单详情
     * @param orderId
     * @param status
     * @return
     */
    OrderInfo getOrderInfoWithStatus(Long orderId, OrderStatus status);
}
