package com.wjl.gmall.mq.receiver;

import com.rabbitmq.client.Channel;
import com.wjl.gmall.common.config.demo.DeadLetterMQConfig;
import com.wjl.gmall.common.config.demo.MqDelayedConfig;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/29
 * @description
 */
@Component
public class ConfirmReceiver {

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "test.queue", durable = "true", autoDelete = "false"),
                    exchange = @Exchange(name = "test.exchange", type = ExchangeTypes.TOPIC, autoDelete = "false", durable = "true"),
                    key = "test.key"
            )
    )
    public void process(String message, Message msg, Channel channel) throws IOException {
        System.out.println("消费者收到" + message);
        System.out.println("msg:" + new String(msg.getBody()));
        // 手动确认
        channel.basicAck(msg.getMessageProperties().getDeliveryTag(), false);
    }


    @RabbitListener(queues = DeadLetterMQConfig.queue_dead_2)
    public void listenDeadMessage(String deadMessage,Message message,Channel channel) throws IOException {
        System.out.println(LocalDateTime.now());
        System.out.println("dead_queue2:" + deadMessage);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        channel.basicAck(deliveryTag,false);
    }

    @RabbitListener(queues = MqDelayedConfig.queue_delay_1)
    public void listenDelayMessage(String deadMessage,Message message,Channel channel) throws IOException {
        System.out.println("dealyMessage:" + deadMessage +" time:" + LocalDateTime.now());
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        channel.basicAck(deliveryTag,false);
    }
}
