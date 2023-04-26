package com.wjl.gmall.weball.web;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/26
 * @description
 */

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    /**
     * 登录控制器
     * 前端的控制 window.location.href = 'http://list.gmall.com/list.html?keyword=' + this.keyword
     * 1. 没有登录，访问该控制器
     * 2. 控制器接收originUrl并保存域中跳转login.html
     * 3. 前端携带该originUrl 访问认证服务 http://service-user/api/user/passport
     * 4. 认证成功后重定向至originUrl
     * @param originUrl 登录成功跳转的页面
     * @return
     */
    @RequestMapping("login.html")
    public String login(@RequestParam("originUrl") String originUrl, Model model) {
        model.addAttribute("originUrl",originUrl);
        return "login";
    }
}
