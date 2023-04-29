package com.wjl.gmall.list.recevier;

import com.rabbitmq.client.Channel;
import com.wjl.gmall.common.constants.MqConst;
import com.wjl.gmall.common.service.RabbitService;
import com.wjl.gmall.list.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
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
 * @date 2023/4/29
 * @description
 */
@Component
public class ListRecevier {

    @Autowired
    private SearchService searchService;

    @Autowired
    private RabbitService rabbitService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = MqConst.QUEUE_GOODS_UPPER, durable = "true", autoDelete = "false"),
                    exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_GOODS, type = ExchangeTypes.DIRECT, autoDelete = "false", durable = "true"),
                    key = MqConst.ROUTING_GOODS_UPPER
            )
    )
    public void upperGoods(Long skuId, Message message, Channel channel) throws Exception {
        try {
            if (skuId != null) {
                searchService.upperGoods(skuId);
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            rabbitService.ack(message);
        } catch (Exception e) {
            // 运行重试
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = MqConst.QUEUE_GOODS_LOWER, durable = "true", autoDelete = "false"),
                    exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_GOODS, type = ExchangeTypes.DIRECT, autoDelete = "false", durable = "true"),
                    key = MqConst.ROUTING_GOODS_LOWER
            )
    )
    public void lowerGoods(Long skuId, Message message, Channel channel) throws Exception {
        try {
            if (skuId != null) {
                searchService.lowerGoods(skuId);
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            rabbitService.ack(message);
        } catch (Exception e) {
            // 运行重试
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            throw new RuntimeException(e);
        }
    }

}
