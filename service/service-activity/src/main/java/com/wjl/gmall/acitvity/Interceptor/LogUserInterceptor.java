package com.wjl.gmall.acitvity.Interceptor;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/4
 * @description
 */

public class LogUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<String> userId = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String user = request.getHeader("userId");
        if (StringUtils.hasText(user)) {
            userId.set(user);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        userId.remove();
    }
}
