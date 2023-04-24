package com.wjl.gmall.product.client.model.dto;

import com.wjl.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * BaseAttrInfo
 * </p>
 *
 */
@Data
@ApiModel(description = "平台属性")

public class BaseAttrInfo extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "属性名称")
	private String attrName;

	@ApiModelProperty(value = "分类id")
	private Long categoryId;

	@ApiModelProperty(value = "分类层级")
	private Integer categoryLevel;

	//	平台属性值集合
	private List<BaseAttrValue> attrValueList;

}

