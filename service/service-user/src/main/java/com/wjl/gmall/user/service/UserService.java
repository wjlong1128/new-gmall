package com.wjl.gmall.user.service;

import com.wjl.gmall.user.model.entity.UserAddress;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/27
 * @description
 */
public interface UserService {
    List<UserAddress> findUserAddressList(Long userId);

}
