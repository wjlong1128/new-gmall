package com.wjl.gmall.acitvity.config;

import com.wjl.gmall.acitvity.Interceptor.LogUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/4
 * @description
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogUserInterceptor()).addPathPatterns("/**");
    }
}
