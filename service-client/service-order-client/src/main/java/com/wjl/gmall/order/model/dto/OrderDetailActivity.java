package com.wjl.gmall.order.model.dto;

import com.wjl.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "订单活动关联表")
public class OrderDetailActivity extends BaseEntity {
   
   private static final long serialVersionUID = 1L;
   
   @ApiModelProperty(value = "订单id")
   private Long orderId;

   @ApiModelProperty(value = "订单明细id")
   private Long orderDetailId;

   @ApiModelProperty(value = "活动ID")
   private Long activityId;

   @ApiModelProperty(value = "活动规则")
   private Long activityRule;

   @ApiModelProperty(value = "skuID")
   private Long skuId;

   @ApiModelProperty(value = "创建时间")
   private Date createTime;

}