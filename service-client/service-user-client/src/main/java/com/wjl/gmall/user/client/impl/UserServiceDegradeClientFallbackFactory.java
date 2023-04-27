package com.wjl.gmall.user.client.impl;

import com.wjl.gmall.user.client.UserServiceClient;
import com.wjl.gmall.user.dto.UserAddress;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/27
 * @description
 */
@Component
public class UserServiceDegradeClientFallbackFactory implements FallbackFactory<UserServiceClient> {
    @Override
    public UserServiceClient create(Throwable cause) {
        return new UserServiceClient() {
            @Override
            public List<UserAddress> findUserAddressListByUserId(Long userId) {
                return null;
            }
        };
    }
}
