package com.wjl.gmall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wjl.gmall.user.model.entity.UserAddress;
import com.wjl.gmall.user.mapper.UserAddressMapper;
import com.wjl.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/27
 * @description
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserAddressMapper userAddressMapper;

    /**
     * 获取用户地址
     *
     * @param userId
     * @return
     */
    @Override
    public List<UserAddress> findUserAddressList(Long userId) {
        LambdaQueryWrapper<UserAddress> wrapper = Wrappers.lambdaQuery(UserAddress.class).eq(UserAddress::getUserId, userId);
        return userAddressMapper.selectList(wrapper);
    }

}
