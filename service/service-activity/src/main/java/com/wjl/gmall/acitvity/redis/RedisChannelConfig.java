package com.wjl.gmall.acitvity.redis;

import com.wjl.gmall.acitvity.recevier.RedisMessageReceive;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/3
 * @description
 */
@Configuration
public class RedisChannelConfig {

    public static final String SECKILL_PUBLISH = "seckillPush";


    @Bean
    public MessageListenerAdapter messageListenerAdapter(RedisMessageReceive messageReceive) {
        /**
         * 指定接收消息的类和方法
         */
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(messageReceive,"receiveMessage");
        return messageListenerAdapter;
    }

    @Bean
    public RedisMessageListenerContainer seckillListenerContainer(
            MessageListenerAdapter messageListenerAdapter,
            RedisConnectionFactory factory
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.addMessageListener(messageListenerAdapter, new PatternTopic(SECKILL_PUBLISH));
        container.setConnectionFactory(factory);
        return container;
    }

}
