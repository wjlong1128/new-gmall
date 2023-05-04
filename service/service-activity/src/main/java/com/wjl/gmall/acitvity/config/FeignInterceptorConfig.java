package com.wjl.gmall.acitvity.config;

import com.wjl.gmall.acitvity.Interceptor.LogUserInterceptor;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/4
 * @description
 */
@Configuration
public class FeignInterceptorConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                if (StringUtils.hasText(LogUserInterceptor.userId.get())) {
                    template.header("userId", LogUserInterceptor.userId.get());
                }
                //ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                //String userId = requestAttributes.getRequest().getHeader("userId");
                //if (StringUtils.hasText(userId)) {
                //    template.header("userId", userId);
                //}
            }
        };
    }

}
