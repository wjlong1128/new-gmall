package com.wjl.gmall.order.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/30
 * @description
 */
@Configuration
public class OrderCancelMqConfig {

    public static final String ORDER_CANCEL_EXCHANGE = "order.cancel.exchange";
    public static final String ORDER_CANCEL_QUEUE = "order.cancel.queue";
    public static final String ORDER_CANCEL_KEY = "order.cancel.key";
    public static final String ORDER_CANCEL_PROCESS_QUEUE = "order.cancel.process.queue";


    public static final String ORDER_CANCEL_PROCESS_KEY = "order.cancel.process.key";


    /**
     * 交换机
     *
     * @return
     */
    @Bean
    public DirectExchange orderCancelExchange() {
        return new DirectExchange(ORDER_CANCEL_EXCHANGE, true, false);
    }

    /**
     * 等待消息超时的队列
     *
     * @return
     */
    @Bean
    public Queue orderCancelQueue() {
        return QueueBuilder
                .durable(ORDER_CANCEL_QUEUE)
                // 一分钟
                // .ttl(1000 * 60 * 1)
                // 十秒
                .ttl(1000 * 60 * 30)
                // 死信交换机
                .deadLetterExchange(ORDER_CANCEL_EXCHANGE)
                // 死信队列
                .deadLetterRoutingKey(ORDER_CANCEL_PROCESS_KEY)
                .build();
    }

    /**
     * 延迟处理消息的队列
     *
     * @return
     */
    @Bean
    public Queue orderCancelProcessQueue() {
        return new Queue(ORDER_CANCEL_PROCESS_QUEUE, true, false, false);
    }

    @Bean
    public Binding orderCancelBinding() {
        return BindingBuilder.bind(orderCancelQueue()).to(orderCancelExchange()).with(ORDER_CANCEL_KEY);
    }

    @Bean
    public Binding orderCancelProcessBinding() {
        return BindingBuilder.bind(orderCancelProcessQueue()).to(orderCancelExchange()).with(ORDER_CANCEL_PROCESS_KEY);
    }
}
