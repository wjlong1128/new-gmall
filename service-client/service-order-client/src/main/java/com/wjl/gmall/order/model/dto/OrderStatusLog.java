package com.wjl.gmall.order.model.dto;

import com.wjl.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "订单状态记录")
public class OrderStatusLog extends BaseEntity {
   
   private static final long serialVersionUID = 1L;
   
   @ApiModelProperty(value = "orderId")
   private Long orderId;

   @ApiModelProperty(value = "orderStatus")
   private String orderStatus;

   @ApiModelProperty(value = "operateTime")
   private Date operateTime;

}