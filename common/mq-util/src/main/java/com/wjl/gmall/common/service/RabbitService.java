package com.wjl.gmall.common.service;

import com.alibaba.fastjson.JSON;
import com.wjl.gmall.common.model.message.GmallCorrelationData;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/29
 * @description
 */
@Service
public class RabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 发送普通消息
     *
     * @param exchange
     * @param routingKey
     * @param message
     * @return
     */
    public boolean sendMessage(String exchange, String routingKey, Object message) {
        GmallCorrelationData data = GmallCorrelationData.setting(exchange, routingKey, message);
        sendMessage(data);
        return true;
    }

    /**
     * 发送延时消息
     *
     * @param exchangeDelay
     * @param routingDelay
     * @param message
     * @param delayTime
     * @param unit
     */
    public void sendDelayMessage(String exchangeDelay, String routingDelay, String message, int delayTime, TimeUnit unit) {
        GmallCorrelationData data = GmallCorrelationData.setting(exchangeDelay, routingDelay, message, (int) unit.toMillis(delayTime));
        sendMessage(data);
    }

    /**
     * 确认消息收到
     *
     * @param message
     */
    public void ack(Message message) {
        String messageId = (String) message.getMessageProperties().getHeaders().get("spring_returned_message_correlation");
        stringRedisTemplate.delete("message:" + messageId);
    }


    /**
     * 私有构造消息发送
     *
     * @param data
     */
    private void sendMessage(GmallCorrelationData data) {
        String messageJson = JSON.toJSONString(data);
        // 存储消息
        stringRedisTemplate.opsForValue().set("message:" + data.getId(), messageJson, 10, TimeUnit.MINUTES);
        String exchange = data.getExchange();
        String routingKey = data.getRoutingKey();
        Object message = data.getMessage();

        if (!data.isDelay()) {
            rabbitTemplate.convertAndSend(exchange, routingKey, message, data);
            return;
        }
        // 延迟消息
        int delayTime = data.getDelayTime();
        //rabbitTemplate.convertAndSend();
        rabbitTemplate.convertAndSend(exchange, routingKey, message, msg -> {
            msg.getMessageProperties().setDelay(delayTime);
            return msg;
        }, data);
    }

}
