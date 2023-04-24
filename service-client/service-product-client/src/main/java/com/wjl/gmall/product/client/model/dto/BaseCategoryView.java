//
//
package com.wjl.gmall.product.client.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * BaseCategoryView
 * </p>
 *
 */
@Data
@ApiModel(description = "BaseCategoryView")

public class BaseCategoryView implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;
	
	@ApiModelProperty(value = "一级分类编号")

	private Long category1Id;

	@ApiModelProperty(value = "一级分类名称")
	private String category1Name;

	@ApiModelProperty(value = "二级分类编号")
	private Long category2Id;

	@ApiModelProperty(value = "二级分类名称")
	private String category2Name;

	@ApiModelProperty(value = "三级分类编号")
	private Long category3Id;

	@ApiModelProperty(value = "三级分类名称")
	private String category3Name;

}

