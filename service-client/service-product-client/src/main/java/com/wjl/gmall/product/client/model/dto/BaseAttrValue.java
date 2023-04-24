package com.wjl.gmall.product.client.model.dto;

import com.wjl.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * BaseAttrValue
 * </p>
 *
 */
@Data
@ApiModel(description = "平台属性值")
public class BaseAttrValue extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "属性值名称")
	private String valueName;

	@ApiModelProperty(value = "属性id")
	private Long attrId;
}

