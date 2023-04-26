package com.wjl.gmall.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/26
 * @description
 */
@SpringBootTest(classes = UserApplication.class)
public class UserApplicationTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    void contentLoad() {
        System.out.println(applicationContext.getBean(DataSourceAutoConfiguration.class));
    }
}
