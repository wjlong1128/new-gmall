package com.wjl.gmall.common.config;

import com.alibaba.fastjson.JSON;
import com.wjl.gmall.common.model.message.GmallCorrelationData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/29
 * @description
 */
@Component
@Slf4j
public class MQProductAckConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback/*, ApplicationContextAware */ {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setReturnCallback(this);
        rabbitTemplate.setConfirmCallback(this);
    }

    /**
     * 确认到达交换机
     *
     * @param correlationData correlation data for the callback.
     * @param ack             true for ack, false for nack
     * @param cause           An optional cause, for nack, when available, otherwise null.
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println(ack);
        if (ack) {
            log.info("消息发送到达交换机成功:{}", JSON.toJSONString(correlationData));
        } else {
            // System.out.println("class:"+correlationData.getClass()); class:class com.wjl.gmall.common.model.message.GmallCorrelationData
            this.retrySendMessage(correlationData, () -> {
                log.error("消息发送到达交换机失败 cause:{}  message:{}", cause, JSON.toJSONString(correlationData));
            });
        }
    }


    /**
     * 确认到达队列 只有失败执行
     *
     * @param message    the returned message.
     * @param replyCode  the reply code.
     * @param replyText  the reply text.
     * @param exchange   the exchange.
     * @param routingKey the routing key.
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        //System.out.println(message.getMessageProperties().getMessageId()); // null
        String springReturnedMessageCorrelation = "spring_returned_message_correlation";
        String messageId = message.getMessageProperties().getHeader(springReturnedMessageCorrelation);
        // 到达队列前已经成功到达交换机，无法反序列化，只能通过redis
        String json = redisTemplate.opsForValue().get("message:" + messageId);
        GmallCorrelationData gmallCorrelationData = JSON.parseObject(json, GmallCorrelationData.class);
        this.retrySendMessage(gmallCorrelationData, () -> {
            log.error("消息到达队列失败 message:{} replyCode:{} replyText:{} exchange:{} routingKey:{}", message, replyCode, replyText, exchange, routingKey);
        });
    }


    /**
     * 重试发送消息
     *
     * @param correlationData
     */
    private void retrySendMessage(CorrelationData correlationData, SendMessageError errorProcess) {
        if (correlationData instanceof GmallCorrelationData) {
            GmallCorrelationData data = (GmallCorrelationData) correlationData;
            int retryCount = data.getRetryCount();
            if (retryCount <= data.getMaxRetryCount()) {
                retryCount += 1;
                data.setRetryCount(retryCount);
                // 重发消息
                rabbitTemplate.convertAndSend(data.getExchange(), data.getRoutingKey(), data.getMessage(), data);
                // 更新至redis
                redisTemplate.opsForValue().set("message:" + data.getId(), JSON.toJSONString(data));
            } else {
                errorProcess.process();
                String msg = JSON.toJSONString(data);
                log.error("消息重试最大次数依然失败:{}", msg);
                // 缓存到 redis
                redisTemplate.opsForValue().set("err:msg:" + data.getMessageId(), msg);
            }
        } else {
            log.error("correlationData not a GmallCorrelationData");
            errorProcess.process();
        }

    }

    public static interface SendMessageError {
        void process();
    }
}


/**
 * ApplicationContext
 *
 * @param applicationContext the ApplicationContext object to be used by this object
 * @throws org.springframework.beans.BeansException
 */
//@Override
//public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//    RabbitTemplate bean = applicationContext.getBean(RabbitTemplate.class);
//    bean.setConfirmCallback(this);
//    bean.setReturnCallback(this);
//}



