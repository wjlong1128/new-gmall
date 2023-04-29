package com.wjl.gmall.common.config.test;

import java.lang.annotation.*;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/29
 * @description
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Wjl {
    String value();
}
