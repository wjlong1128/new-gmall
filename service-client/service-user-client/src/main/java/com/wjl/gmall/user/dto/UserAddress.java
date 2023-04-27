package com.wjl.gmall.user.dto;

import com.wjl.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "用户地址")
public class UserAddress extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户地址")
    private String userAddress;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "收件人")
    private String consignee;

    @ApiModelProperty(value = "联系方式")
    private String phoneNum;

    @ApiModelProperty(value = "是否是默认")
    private String isDefault;

}

