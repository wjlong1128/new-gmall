package com.wjl.gmall.order.receiver;

import com.rabbitmq.client.Channel;
import com.wjl.gmall.model.enums.PaymentType;
import com.wjl.gmall.order.config.OrderCancelMqConfig;
import com.wjl.gmall.order.model.entity.OrderInfo;
import com.wjl.gmall.order.service.OrderService;
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
        String unpaid = "UNPAID";
        if (unpaid.equals(processStatus) && "UNPAID".equals(orderStatus)) {
            // 调用接口关闭订单
            try {
                // 问题，如果用户在刚好超时的情况下付款了，那么怎么判断到底有没有支付呢？

                //1. 再次 调用远程，查看是否有交易信息

                // 1.1 有 ？ 再次查询支付宝交易记录

                // 1.1.1 有？ 支付宝 交易记录 订单
                // 1.1.2 没有 关闭订单支付记录

                // 1.2 没有 ？ 只关闭订单

                orderService.execExpireOrder(orderInfoId, "2", PaymentType.ALIPAY);
            } catch (Exception e) {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                throw new RuntimeException(e);
            }
        }
        // 手动确认接收消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
