package com.wjl.gmall.common.config.demo;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/29
 * @description
 */
@Configuration
public class DeadLetterMQConfig {

    public static final String exchange_dead = "exchange.dead";
    public static final String routing_dead_1 = "routing.dead.1";
    public static final String routing_dead_2 = "routing.dead.2";
    public static final String queue_dead_1 = "queue.dead.1";
    public static final String queue_dead_2 = "queue.dead.2";

    @Bean
    public TopicExchange deadExchange() {
        return new TopicExchange(exchange_dead, true, false);
    }

    @Bean// 此队列必须无人监听
    public Queue deadQueue1() {
        HashMap<String, Object> args = new HashMap<>();
        //// 参数绑定 此处的key 固定值，不能随意写
        //map.put("x-dead-letter-exchange",exchange_dead);
        //map.put("x-dead-letter-routing-key",routing_dead_2);
        //// 设置延迟时间
        //map.put("x-message-ttl", 10 * 1000);
        return QueueBuilder
                .durable(queue_dead_1)
                // 死信交换机
                .ttl(1000 * 5)
                // 超时5秒没有消费者消费消息，那么就会路由到这个routing指定的key
                .deadLetterExchange(exchange_dead)
                .deadLetterRoutingKey(routing_dead_2)
                .build();
    }


    @Bean
    public Queue deadQueue2() {
        return QueueBuilder.durable(queue_dead_2).build();
    }

    @Bean
    public Binding deadQueue1Bind() {
        return BindingBuilder.bind(deadQueue1()).to(deadExchange()).with(routing_dead_1);
    }

    @Bean
    public Binding deadQueue2Bind() {
        return BindingBuilder.bind(deadQueue2()).to(deadExchange()).with(routing_dead_2);
    }

}
