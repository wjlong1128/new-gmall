package com.wjl.gmall.order.receiver;

import com.rabbitmq.client.Channel;
import com.wjl.gmall.model.enums.OrderStatus;
import com.wjl.gmall.model.enums.PaymentType;
import com.wjl.gmall.order.config.OrderCancelMqConfig;
import com.wjl.gmall.order.model.entity.OrderInfo;
import com.wjl.gmall.order.service.OrderService;
import com.wjl.gmall.payment.client.PaymentServiceClient;
import com.wjl.gmall.payment.dto.PaymentInfo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/30
 * @description
 */
@Component
public class OrderCancelReceiver {

    @Autowired
    private OrderService orderService;


    @Autowired
    private PaymentServiceClient paymentServiceClient;

    /**
     * 订单超时
     * 已支付 不操作
     * 未支付 修改订单状态
     *
     * @param orderInfoId
     * @param message
     * @param channel
     */
    @RabbitListener(queues = OrderCancelMqConfig.ORDER_CANCEL_PROCESS_QUEUE)
    public void orderCancelProcess(Long orderInfoId, Message message, Channel channel) throws IOException {
        if (orderInfoId == null) {
            // 手动确认接收消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }
        OrderInfo orderInfo = orderService.getById(orderInfoId);
        if (orderInfoId == null) {
            // 手动确认接收消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }

        String processStatus = orderInfo.getProcessStatus();
        String orderStatus = orderInfo.getOrderStatus();
        // 未支付的状态
        String unpaid = OrderStatus.UNPAID.name();
        if (unpaid.equals(processStatus) && unpaid.equals(orderStatus)) {
            PaymentType type = PaymentType.ALIPAY;
            // 调用接口关闭订单
            try {
                // 问题，如果用户在刚好超时的情况下付款了，那么怎么判断到底有没有支付呢？
                // 查询支付记录信息
                PaymentInfo paymentInfo = paymentServiceClient.getPaymentInfo(orderInfoId, type.name()).getData();
                // 如果支付信息不为空 并且状态为未支付
                if (paymentInfo != null && unpaid.equals(paymentInfo.getPaymentStatus())) {
                    // 查询支付宝的交易记录
                    Boolean isExits = paymentServiceClient.checkPayment(orderInfoId).getData();
                    if (Boolean.TRUE.equals(isExits)) {
                        // 有
                        Boolean closeSuccess = this.paymentServiceClient.closePay(orderInfoId).getData();
                        if (Boolean.TRUE.equals(closeSuccess)) {
                            // 关闭成功 表示扫码了 但是未支付超时 关闭未支付订单和交易信息
                            orderService.execExpireOrder(orderInfoId, "2", type);
                        } else {
                            // 已经支付了 无法关闭
                            // 不做处理。。。
                        }
                    } else {
                        // 没有 关闭订单 关闭记录
                        orderService.execExpireOrder(orderInfoId, "2", type);
                    }
                } else {
                    // 只关闭订单 不用关闭订单记录，因为没有
                    orderService.execExpireOrder(orderInfoId, "1", type);
                }
            } catch (Exception e) {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                throw new RuntimeException(e);
            }
        }
        // 手动确认接收消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
