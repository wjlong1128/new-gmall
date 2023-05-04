package com.wjl.gmall.cart.model.dto;

import com.wjl.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(description = "活动规则")
public class ActivityRule extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "类型")
    private Long activityId;

    @ApiModelProperty(value = "满减金额")
    private BigDecimal conditionAmount;

    @ApiModelProperty(value = "满减件数")
    private Long conditionNum;

    @ApiModelProperty(value = "优惠金额")
    private BigDecimal benefitAmount;

    @ApiModelProperty(value = "优惠折扣")
    private BigDecimal benefitDiscount;

    @ApiModelProperty(value = "优惠级别")
    private Long benefitLevel;

    @ApiModelProperty(value = "活动类型（1：满减，2：折扣）")
    private String activityType;

    // 添加一个skuId
    @ApiModelProperty(value = "活动skuId")
    private Long skuId;

    @ApiModelProperty(value = "优惠后减少金额")
    private BigDecimal reduceAmount;

    @ApiModelProperty(value = "活动对应的skuId列表")
    private List<Long> skuIdList;
}