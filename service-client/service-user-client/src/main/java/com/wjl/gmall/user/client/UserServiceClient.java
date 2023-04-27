package com.wjl.gmall.user.client;

import com.wjl.gmall.user.dto.UserAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/27
 * @description
 */
@FeignClient(value = "service-user")
public interface UserServiceClient {

    /**
     *  获取用户地址列表
     * @param userId
     * @return
     */
    @GetMapping("api/user/inner/findUserAddressListByUserId/{userId}")
    public List<UserAddress> findUserAddressListByUserId(@PathVariable("userId") Long userId);
}
