package com.wjl.gmall.product.client.model.dto;


import com.wjl.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * SpuPoster
 * </p>
 *

 */
@Data
@ApiModel(description = "SpuPoster")
public class SpuPoster extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "商品id")
	private Long spuId;

	@ApiModelProperty(value = "文件名称")
	private String imgName;

	@ApiModelProperty(value = "文件路径")
	private String imgUrl;

}

