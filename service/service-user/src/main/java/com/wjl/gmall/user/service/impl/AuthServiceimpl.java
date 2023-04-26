package com.wjl.gmall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wjl.gmall.common.constant.RedisConst;
import com.wjl.gmall.user.mapper.UserInfoMapper;
import com.wjl.gmall.user.model.dto.AuthDTO;
import com.wjl.gmall.user.model.entity.UserInfo;
import com.wjl.gmall.user.model.vo.UserResponse;
import com.wjl.gmall.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/26
 * @description
 */
@Service
public class AuthServiceimpl implements AuthService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public UserResponse login(UserInfo userInfo, String ip, String agent) {
        String newPass = DigestUtils.md5DigestAsHex(userInfo.getPasswd().getBytes());
        LambdaQueryWrapper<UserInfo> wrapper = Wrappers.lambdaQuery(UserInfo.class).eq(UserInfo::getLoginName, userInfo.getLoginName()).eq(UserInfo::getPasswd, newPass);
        UserInfo user = userInfoMapper.selectOne(wrapper);
        if (user == null) {
            return null;
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        AuthDTO authDTO = new AuthDTO(user.getId(), agent, ip);
        redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_KEY_PREFIX + token, authDTO.toJson(), RedisConst.USERKEY_TIMEOUT, TimeUnit.SECONDS);
        UserResponse userResponse = new UserResponse();
        userResponse.setToken(token);
        userResponse.setNickName(user.getNickName());
        return userResponse;
    }

    @Override
    public void logout(String token) {
        redisTemplate.delete(RedisConst.USER_LOGIN_KEY_PREFIX + token);
    }
}
