package com.wjl.gmall.product.client.model.dto;

import com.wjl.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * SkuImage
 * </p>
 *
 */
@Data
@ApiModel(description = "Sku图片")
public class SkuImage extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "商品id")
	private Long skuId;

	@ApiModelProperty(value = "图片名称（冗余）")
	private String imgName;

	@ApiModelProperty(value = "图片路径(冗余)")
	private String imgUrl;

	@ApiModelProperty(value = "商品图片id")
	private Long spuImgId;

	@ApiModelProperty(value = "是否默认")
	private String isDefault;

}

