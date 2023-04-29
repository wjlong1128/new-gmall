package com.wjl.gmall.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/29
 * @description
 */
@Configuration
public class MessageConvertConfig {

    @Bean
    public MessageConverter messageConverter(){
        return new MappingJackson2MessageConverter();
    }

}
