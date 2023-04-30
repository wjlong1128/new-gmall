package com.wjl.gmall.order.receiver;

import com.rabbitmq.client.Channel;
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
                orderService.execExpireOrder(orderInfoId);
            } catch (Exception e) {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                throw new RuntimeException(e);
            }
        }
        // 手动确认接收消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
