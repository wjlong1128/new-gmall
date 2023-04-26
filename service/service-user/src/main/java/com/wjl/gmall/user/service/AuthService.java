package com.wjl.gmall.user.service;

import com.wjl.gmall.user.model.entity.UserInfo;
import com.wjl.gmall.user.model.vo.UserResponse;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/26
 * @description
 */
public interface AuthService {
    UserResponse login(UserInfo userInfo, String ip, String agent);

    void logout(String token);
}
