package com.wjl.gmall.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/29
 * @description
 */
@Component
public class ApplicationContextHolder implements ApplicationContextAware {
    private  static ApplicationContext context;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
       context = applicationContext;
    }

    public static <T> T getBean(Class<T> t){
        return context.getBean(t);
    }
}
