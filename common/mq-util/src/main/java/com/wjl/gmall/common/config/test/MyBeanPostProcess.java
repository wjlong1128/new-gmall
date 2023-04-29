package com.wjl.gmall.common.config.test;

import com.wjl.gmall.common.config.test.Wjl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/29
 * @description
 */
// @Component
public class MyBeanPostProcess implements BeanPostProcessor {
    /**
     * @param bean the new bean instance
     * @param beanName the name of the bean
     * @return
     * @throws BeansException
     */

    @Autowired
    private Environment env;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        for (Field declaredField : bean.getClass().getDeclaredFields()){
            declaredField.setAccessible(true);
            Wjl annotation = declaredField.getAnnotation(Wjl.class);
            if(annotation != null && declaredField.getType().equals(String.class)){
                try {
                    String property = env.getProperty(annotation.value());
                    declaredField.set(bean,property);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return bean;
    }
}
