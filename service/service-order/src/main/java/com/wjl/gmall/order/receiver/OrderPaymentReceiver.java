package com.wjl.gmall.order.receiver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import com.wjl.gmall.common.constants.MqConst;
import com.wjl.gmall.model.enums.ProcessStatus;
import com.wjl.gmall.order.model.entity.OrderInfo;
import com.wjl.gmall.order.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/2
 * @description
 */
@Component
public class OrderPaymentReceiver {

    @Autowired
    private OrderService orderService;


    /**
     * 订单支付监听
     *
     * @param orderId
     * @param message
     * @param channel
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = MqConst.QUEUE_PAYMENT_PAY),
                    exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_PAYMENT_PAY),
                    key = MqConst.ROUTING_PAYMENT_PAY
            )
    )
    public void orderPay(Long orderId, Message message, Channel channel) throws IOException {
        try {
            if (orderId != null) {
                OrderInfo info = orderService.getById(orderId);
                if (info != null && info.getOrderStatus().equals(ProcessStatus.UNPAID.name())) {
                    orderService.updateOrderProcessStatus(orderId, ProcessStatus.PAID);
                    // 支付成功 扣减库存
                    orderService.sendOrderStatus(info);
                }
            }
        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            throw new RuntimeException(e);
        }

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 扣减库存成功
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = MqConst.QUEUE_WARE_ORDER),
                    exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_WARE_ORDER),
                    key = MqConst.ROUTING_WARE_ORDER
            )
    )
    public void stockOrderStatus(String json, Message message, Channel channel) throws IOException {
        try {
            if (StringUtils.hasText(json)) {
                // 转换数据类型
                Map<String, Object> map = JSON.parseObject(json, new TypeReference<Map<String, Object>>() {
                });

                // 获取订单id
                String orderId = (String) map.get("orderId");
                String status = (String) map.get("status");
                // 是待发货状态
                if (orderId != null && status.equals("DEDUCTED")) {
                    this.orderService.updateOrderProcessStatus(Long.parseLong(orderId), ProcessStatus.WAITING_DELEVER);
                } else {
                    // 库存不足
                    this.orderService.updateOrderProcessStatus(Long.parseLong(orderId), ProcessStatus.STOCK_EXCEPTION);
                    // 通知人工
                }
            }
        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            throw new RuntimeException(e);
        }

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }



    /**
     * 订单取消
     *
     * @param orderId
     * @param message
     * @param channel
     */
    //@RabbitListener(
    //        bindings = @QueueBinding(
    //                value = @Queue(name = MqConst.QUEUE_PAYMENT_CLOSE),
    //                exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_PAYMENT_CLOSE),
    //                key = MqConst.ROUTING_PAYMENT_CLOSE
    //        )
    //)
    //public void orderClose(Long orderId, Message message, Channel channel) throws IOException {
    //    try {
    //        if (orderId != null) {
    //            OrderInfo info = orderService.getById(orderId);
    //            if (info != null && !info.getOrderStatus().equals(ProcessStatus.CLOSED.name())) {
    //                orderService.updateOrderProcessStatus(orderId, ProcessStatus.CLOSED);
    //            }
    //        }
    //    } catch (Exception e) {
    //        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
    //        throw new RuntimeException(e);
    //    }
    //
    //    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    //}

}
