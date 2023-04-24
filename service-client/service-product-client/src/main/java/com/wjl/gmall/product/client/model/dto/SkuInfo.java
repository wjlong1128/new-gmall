package com.wjl.gmall.product.client.model.dto;

import com.wjl.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * SkuInfo
 * </p>
 *
 */
@Data
@ApiModel(description = "SkuInfo")
public class SkuInfo extends BaseEntity {


    public SkuInfo(){}
	public SkuInfo(Long skuId){
		setId(skuId);
	}
	//	判断去重的话，自动调用equals 方法。
	public boolean equals(SkuInfo skuInfo){
		return getId().equals(skuInfo.getId());
	}

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "商品id")
	private Long spuId;

	@ApiModelProperty(value = "价格")
	private BigDecimal price;

	@ApiModelProperty(value = "sku名称")
	private String skuName;

	@ApiModelProperty(value = "商品规格描述")
	private String skuDesc;

	@ApiModelProperty(value = "重量")
	private String weight;

	@ApiModelProperty(value = "品牌(冗余)")
	private Long tmId;

	@ApiModelProperty(value = "三级分类id（冗余)")
	private Long category3Id;

	@ApiModelProperty(value = "默认显示图片(冗余)")
	private String skuDefaultImg;

	@ApiModelProperty(value = "是否销售（1：是 0：否）")
	private Integer isSale;

	List<SkuImage> skuImageList;

	List<SkuAttrValue> skuAttrValueList;

	List<SkuSaleAttrValue> skuSaleAttrValueList;
}

