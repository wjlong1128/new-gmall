package com.wjl.gmall.product.client.model.dto;

import com.wjl.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * <p>
 * SpuSaleAttrValue
 * </p>
 *
 */
@Data
@ApiModel(description = "销售属性值")
public class SpuSaleAttrValue extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "商品id")
	private Long spuId;

	@ApiModelProperty(value = "销售属性id")
	private Long baseSaleAttrId;

	@ApiModelProperty(value = "销售属性值名称")
	private String saleAttrValueName;

	@ApiModelProperty(value = "销售属性名称(冗余)")
	private String saleAttrName;

	// 是否是默认选中状态
	//@TableField("sale_attr_name")
	//String isChecked;
	String isChecked;

}

