package com.wjl.gmall.product.client.model.dto;


import com.wjl.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * SpuSaleAttr
 * </p>
 *
 */
@Data
@ApiModel(description = "销售属性")

public class SpuSaleAttr extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "商品id")
	private Long spuId;

	@ApiModelProperty(value = "销售属性id")
	private Long baseSaleAttrId;

	@ApiModelProperty(value = "销售属性名称(冗余)")
	private String saleAttrName;

	// 销售属性值对象集合
	List<SpuSaleAttrValue> spuSaleAttrValueList;

}

