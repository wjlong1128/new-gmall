package com.wjl.gmall.order.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wjl.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "CouponInfo")
public class CouponInfo extends BaseEntity {
   
   private static final long serialVersionUID = 1L;
   
   @ApiModelProperty(value = "购物券名称")
   private String couponName;

   @ApiModelProperty(value = "购物券类型")
   private String couponType;

   @ApiModelProperty(value = "满额数")
   private BigDecimal conditionAmount;

   @ApiModelProperty(value = "满件数")
   private Long conditionNum;

   @ApiModelProperty(value = "活动编号")
   private Long activityId;

   @ApiModelProperty(value = "减金额")
   private BigDecimal benefitAmount;

   @ApiModelProperty(value = "折扣")
   private BigDecimal benefitDiscount;

   @ApiModelProperty(value = "范围类型")
   private String rangeType;

   @ApiModelProperty(value = "最多领用次数")
   private Integer limitNum;

   @ApiModelProperty(value = "已领用次数")
   private Integer takenCount;

   @ApiModelProperty(value = "可以领取的开始日期")
   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
   private Date startTime;

   @ApiModelProperty(value = "可以领取的结束日期")
   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
   private Date endTime;

   @ApiModelProperty(value = "创建时间")
   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
   private Date createTime;

   @ApiModelProperty(value = "修改时间")
   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
   private Date operateTime;

   @ApiModelProperty(value = "过期时间")
   @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
   private Date expireTime;

   @ApiModelProperty(value = "优惠券范围描述")
   private String rangeDesc;

   private String couponTypeString;

   private String rangeTypeString;

   @ApiModelProperty(value = "是否领取")
   private Integer isGet;

   @ApiModelProperty(value = "购物券状态（1：未使用 2：已使用）")
   private String couponStatus;

   @ApiModelProperty(value = "范围类型id")
   private Long rangeId;

   @ApiModelProperty(value = "优惠券对应的skuId列表")
   private List<Long> skuIdList;

   @ApiModelProperty(value = "优惠后减少金额")
   private BigDecimal reduceAmount;

   @ApiModelProperty(value = "是否最优选项")
   private Integer isChecked = 0;

   @ApiModelProperty(value = "是否可选")
   private Integer isSelect = 0;

}