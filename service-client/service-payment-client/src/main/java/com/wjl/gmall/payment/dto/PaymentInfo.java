//
//
package com.wjl.gmall.payment.dto;

import com.wjl.gmall.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * PaymentInfo
 * </p>
 */
@Data
@ApiModel(description = "支付信息")
public class PaymentInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "对外业务编号")
    private String outTradeNo;

    @ApiModelProperty(value = "订单编号")
    private Long orderId;

    @ApiModelProperty(value = "用户Id")
    private Long userId;

    @ApiModelProperty(value = "支付类型（微信 支付宝）")
    private String paymentType;

    @ApiModelProperty(value = "交易编号")
    private String tradeNo;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "交易内容")
    private String subject;

    @ApiModelProperty(value = "支付状态")
    private String paymentStatus;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "回调时间")
    private Date callbackTime;

    @ApiModelProperty(value = "回调信息")
    private String callbackContent;

}

