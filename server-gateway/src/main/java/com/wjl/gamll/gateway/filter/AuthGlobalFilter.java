package com.wjl.gamll.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.wjl.gamll.gateway.constant.RedisConst;
import com.wjl.gamll.gateway.model.dto.AuthDTO;
import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.common.result.ResultCodeEnum;
import com.wjl.gmall.common.util.IpUtil;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/26
 * @description
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    // 认证白名单那
    @Value("${authUrls.url}")
    private String authUrl;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();
        // 内部接口禁止访问
        if (matcher.match("/api/**/inner/**", path)) {
            return out(response, ResultCodeEnum.PERMISSION);
        }

        String userId = this.getUserId(request);
        if (userId.equals("-1")) {
            // token被盗用
            return out(response, ResultCodeEnum.ILLEGAL_REQUEST);
        }

        if (StringUtils.isEmpty(userId)) { // 这里证明没有登陆 并且访问 权限接口
            // api接口，异步请求，校验用户必须登录
            if (matcher.match("/api/**/auth/**", path)) {
                return out(response, ResultCodeEnum.LOGIN_AUTH);
            }
            // 没有访问权限接口 但是访问到了白名单中的路径 需要登录之后重定向
            for (String authUrl : authUrl.split(",")) {
                // 当前的url包含登录的控制器域名，但是用户Id 为空
                if (path.indexOf(authUrl) != -1) {
                    //303状态码表示由于请求对应的资源存在着另一个URI，应使用重定向获取请求的资源
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    response.getHeaders().set(HttpHeaders.LOCATION, "http://www.gmall.com/login.html?originUrl=" + request.getURI());
                    // 重定向到登录
                    return response.setComplete();
                }
            }
            // 没有登录，但是也没有访问权限接口
            // 获取用户临时id
            String userTempId = this.getUserTempId(request);
            if (StringUtils.hasText(userTempId)){
                request.mutate().header("userTempId",userTempId);
            }
            return chain.filter(exchange);
        }

        // 登录了的用户 将userId 存放请求头中
        request.mutate().header("userId", userId).build();
        return chain.filter(exchange);
    }

    private String getUserTempId(ServerHttpRequest request) {
        String userTempId = "";
        List<String> list = request.getHeaders().get("userTempId");
        if (!CollectionUtils.isEmpty(list)) {
            userTempId = list.get(0);
        }
        return userTempId;
    }

    /**
     * header cookie redis没有 判断未登录 返回空字符串 ""
     * 有，但是和redis中ip agent 不符合，就是盗用token 返回 "-1"
     * 如果正常 就是 userId
     *
     * @param request
     * @return
     */
    private String getUserId(ServerHttpRequest request) {
        List<String> headers = request.getHeaders().get("token");
        String token = null;
        if (!CollectionUtils.isEmpty(headers)) {
            token = headers.get(0);
        }
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if (!CollectionUtils.isEmpty(cookies)) {
            HttpCookie cookie = cookies.get("token").get(0);
            if (cookie != null) {
                token = cookie.getValue();
            }
        }
        if (!StringUtils.hasText(token)) {
            return "";
        }
        String key = RedisConst.USER_LOGIN_KEY_PREFIX + token;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (!StringUtils.hasText(json)){
            return "";
        }
        AuthDTO auth = AuthDTO.jsonToAuth(json);
        String ip = IpUtil.getGatwayIpAddress(request);
        headers = request.getHeaders().get(HttpHeaders.USER_AGENT);
        String agent = null;
        if (!CollectionUtils.isEmpty(headers)) {
            agent = headers.get(0);
        }
        if (auth.getIp().equals(ip) && auth.getAgent().equals(agent)) {
            return auth.getUserId().toString();
        }
        stringRedisTemplate.delete(key);
        return "-1";
    }

    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum codeEnum) {
        Result<Object> result = Result.build(null, codeEnum);
        byte[] bytes = JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer wrap = response.bufferFactory().wrap(bytes);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8");
        return response.writeWith(Mono.just(wrap));
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
