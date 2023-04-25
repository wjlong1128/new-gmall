package com.wjl.gmall.product.client.model.dto;

import com.wjl.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * BaseTrademark
 * </p>
 */
@Data
@ApiModel(description = "商标品牌")
public class BaseTrademark extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "属性值")
    private String tmName;

    @ApiModelProperty(value = "品牌logo的图片路径")
    private String logoUrl;

}

