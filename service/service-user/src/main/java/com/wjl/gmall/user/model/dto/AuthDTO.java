package com.wjl.gmall.user.model.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/26
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthDTO implements Serializable {
    private Long userId;
    private String agent;
    private String ip;

    public String toJson(){
        JSONObject json = new JSONObject();
        json.put("ip", ip);
        json.put("agent", agent);
        json.put("userId", this.userId);
        return json.toJSONString();
    }

    public AuthDTO jsonToAuth(String json){
        return JSON.parseObject(json,AuthDTO.class);
    }
}
