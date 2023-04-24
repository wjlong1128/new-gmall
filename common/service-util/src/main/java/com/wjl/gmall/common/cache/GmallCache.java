package com.wjl.gmall.common.cache;

import java.lang.annotation.*;

/**
 * @author atguigu-mqx
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited // 被修饰的类的子类可继承该注解
@Documented
public @interface GmallCache {

    //  定义一个数据 sku:skuId
    //  目的用这个前缀要想组成 缓存的key！
    String prefix() default "cache:";

}
