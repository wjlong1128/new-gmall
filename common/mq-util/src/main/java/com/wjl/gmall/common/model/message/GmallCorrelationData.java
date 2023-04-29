package com.wjl.gmall.common.model.message;

import lombok.Data;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.lang.Nullable;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.UUID;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/29
 * @description 包装CorrelationData的可重试的CorrelationData
 */

@Data
public class GmallCorrelationData extends CorrelationData {

    private CorrelationData correlationData;
    private String messageId;

    private Object message;

    private String exchange;
    private String routingKey;
    // 重试次数
    private int retryCount = 0;

    private int maxRetryCount = 3;

    // 是否延迟消息
    private boolean isDelay = false;

    // 延迟时间
    private int delayTime = 10;

    public GmallCorrelationData() {
        this.correlationData = new CorrelationData();
    }

    public GmallCorrelationData(String id) {
        this.correlationData = new CorrelationData(id);
        this.messageId = id;
    }

    @Nullable
    public String getId() {
        return this.correlationData.getId();
    }

    public void setId(String id) {
        this.correlationData.setId(id);
        this.setMessageId(id);
    }

    public void setMessageId(String messageId) {
        this.correlationData.setId(messageId);
        this.messageId = messageId;
    }

    public SettableListenableFuture<Confirm> getFuture() {
        return this.correlationData.getFuture();
    }

    @Nullable
    public Message getReturnedMessage() {
        return this.correlationData.getReturnedMessage();
    }

    public void setReturnedMessage(Message returnedMessage) {
        this.correlationData.setReturnedMessage(returnedMessage);
    }

    public static GmallCorrelationData setting(String exchange, String routingKey, Object message) {
        String id = UUID.randomUUID().toString().replace("-", "");
        GmallCorrelationData data = new GmallCorrelationData();
        data.setId(id);
        data.setExchange(exchange);
        data.setRoutingKey(routingKey);
        data.setMessage(message);
        data.setDelay(false);
        return data;
    }


    public static GmallCorrelationData setting(String exchange, String routingKey, Object message,int delayTime) {
        String id = UUID.randomUUID().toString().replace("-", "");
        GmallCorrelationData data = new GmallCorrelationData();
        data.setId(id);
        data.setExchange(exchange);
        data.setRoutingKey(routingKey);
        data.setMessage(message);
        data.setDelay(true);
        data.setDelayTime(delayTime);
        return data;
    }
}
