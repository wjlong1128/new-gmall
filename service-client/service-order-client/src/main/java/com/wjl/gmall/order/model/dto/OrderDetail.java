package com.wjl.gmall.order.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wjl.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(description = "订单明细")
public class OrderDetail extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单编号")
    private Long orderId;

    @ApiModelProperty(value = "sku_id")
    private Long skuId;

    @ApiModelProperty(value = "sku名称（冗余)")
    private String skuName;

    @ApiModelProperty(value = "图片名称（冗余)")
    private String imgUrl;

    @ApiModelProperty(value = "购买价格(下单时sku价格）")
    private BigDecimal orderPrice;

    @ApiModelProperty(value = "购买个数")
    private Integer skuNum;

    // 是否有足够的库存！
    private String hasStock;

    @ApiModelProperty(value = "操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal splitTotalAmount;

    @ApiModelProperty(value = "促销分摊金额")
    private BigDecimal splitActivityAmount;

    @ApiModelProperty(value = "优惠券分摊金额")
    private BigDecimal splitCouponAmount;

}
