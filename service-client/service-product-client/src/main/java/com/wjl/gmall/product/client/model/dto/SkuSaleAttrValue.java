package com.wjl.gmall.product.client.model.dto;

import com.wjl.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * SkuSaleAttrValue
 * </p>
 *
 */
@Data
@ApiModel(description = "Sku销售属性值")
public class SkuSaleAttrValue extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "库存单元id")
	private Long skuId;

	@ApiModelProperty(value = "spu_id(冗余)")
	private Long spuId;

	@ApiModelProperty(value = "销售属性值id")
	private Long saleAttrValueId;

}

