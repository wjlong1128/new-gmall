package com.wjl.gmall.activity.dto;

import com.wjl.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@ApiModel(description = "SeckillGoods")
public class SeckillGoods extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "spu ID")
    private Long spuId;

    @ApiModelProperty(value = "sku ID")
    private Long skuId;

    @ApiModelProperty(value = "标题")
    private String skuName;

    @ApiModelProperty(value = "商品图片")
    private String skuDefaultImg;

    @ApiModelProperty(value = "原价格")
    private BigDecimal price;

    @ApiModelProperty(value = "秒杀价格")
    private BigDecimal costPrice;

    @ApiModelProperty(value = "添加日期")
    private Date createTime;

    @ApiModelProperty(value = "审核日期")
    private LocalDateTime checkTime;

    @ApiModelProperty(value = "审核状态")
    private String status;

    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "秒杀商品数")
    private Integer num;

    @ApiModelProperty(value = "剩余库存数")
    private Integer stockCount;

    @ApiModelProperty(value = "描述")
    private String skuDesc;


}

