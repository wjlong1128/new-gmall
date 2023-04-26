package com.wjl.gmall.user.mapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wjl.gmall.user.UserApplication;
import com.wjl.gmall.user.model.entity.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/26
 * @description
 */
@SpringBootTest(classes = UserApplication.class)
public class UserInfoMapperTest {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Test
    void testMapper() {
        List<UserInfo> userInfos = userInfoMapper.selectList(Wrappers.query());
        System.out.println(userInfos);
    }
}
