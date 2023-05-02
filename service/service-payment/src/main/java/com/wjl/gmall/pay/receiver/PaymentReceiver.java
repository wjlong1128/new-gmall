package com.wjl.gmall.pay.receiver;

import com.rabbitmq.client.Channel;
import com.wjl.gmall.common.constants.MqConst;
import com.wjl.gmall.model.enums.PaymentType;
import com.wjl.gmall.pay.service.PaymentService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/2
 * @description
 */
@Component
public class PaymentReceiver {

    @Autowired
    private PaymentService paymentService;


    /**
     * 监听订单超时更新订单交易记录状态
     *
     * @param idAndType
     * @param message
     * @param channel
     * @throws Exception
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = MqConst.QUEUE_PAYMENT_CLOSE),
                    exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_PAYMENT_CLOSE),
                    key = MqConst.ROUTING_PAYMENT_CLOSE
            )
    )
    public void closePayment(String idAndType, Message message, Channel channel) throws Exception {
        try {
            String[] split = idAndType.split("_");
            Long orderId = Long.parseLong(split[0]);
            PaymentType type = PaymentType.valueOf(split[1]);
            paymentService.closePayment(orderId, type);
        } catch (IllegalArgumentException e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            throw new RuntimeException(e);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
