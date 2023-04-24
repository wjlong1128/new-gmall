package com.wjl.gmall.product.client.model.dto;

import com.wjl.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * SkuAttrValue
 * </p>
 *
 */
@Data
@ApiModel(description = "Sku平台属性值")

public class SkuAttrValue extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "属性id（冗余)")

	private Long attrId;

	@ApiModelProperty(value = "属性值id")

	private Long valueId;

	@ApiModelProperty(value = "skuid")
	private Long skuId;

}

