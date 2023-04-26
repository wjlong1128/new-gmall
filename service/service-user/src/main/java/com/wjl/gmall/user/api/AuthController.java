package com.wjl.gmall.user.api;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.common.util.IpUtil;
import com.wjl.gmall.user.model.entity.UserInfo;
import com.wjl.gmall.user.model.vo.UserResponse;
import com.wjl.gmall.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/26
 * @description
 */
@RequestMapping("/api/user/passport")
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("login")
    public Result login(@RequestBody UserInfo userInfo, HttpServletRequest request) {
        String ip = IpUtil.getIpAddress(request);
        UserResponse userResponse = authService.login(userInfo, ip, request.getHeader(HttpHeaders.USER_AGENT));
        if (userResponse == null) {
            return Result.fail().message("用户名或者密码错误!!!");
        }
        return Result.ok(userResponse);
    }

    @RequestMapping("logout")
    public Result logout(HttpServletRequest request){
        String token = request.getHeader("token");
        authService.logout(token);
       return Result.ok();
    }

}
