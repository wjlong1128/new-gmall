package com.wjl.gmall.model.user;

import com.wjl.gmall.model.base.BaseEntity;
import lombok.Data;

@Data
public class AuthUser extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String loginName;

    private String nickName;

    private String passwd;

    private String name;

    private String phoneNum;

    private String email;

    private String headImg;

    private String userLevel;

}
