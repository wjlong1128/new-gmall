package com.wjl.gmall.common.config.demo;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/29
 * @description 基于 https://github.com/rabbitmq/rabbitmq-delayed-message-exchange
 * 插件的延时
 */

@Configuration
public class MqDelayedConfig {

    public static final String exchange_delay = "exchange.delay";
    public static final String routing_delay = "routing.delay";
    public static final String queue_delay_1 = "queue.delay.1";


    @Bean
    public Queue delayQueue() {
        return new Queue(queue_delay_1);
    }

    @Bean
    public CustomExchange delayExchange() {
        // 发送x-delayed-message类型的消息
        HashMap<String, Object> arguments = new HashMap<>();
        // 指定交换机类型;
        arguments.put("x-delayed-type", "direct");
        return new CustomExchange(exchange_delay, "x-delayed-message", true, false, arguments);
    }

    @Bean
    public Binding delayBinding() {
        return BindingBuilder.bind(delayQueue()).to(delayExchange()).with(routing_delay).noargs();
    }


}
