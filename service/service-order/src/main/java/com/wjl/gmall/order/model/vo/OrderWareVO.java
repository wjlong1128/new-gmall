package com.wjl.gmall.order.model.vo;

import com.alibaba.fastjson.JSON;
import com.wjl.gmall.model.enums.PaymentType;
import com.wjl.gmall.order.model.entity.OrderInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/2
 * @description
 */
@Getter
public class OrderWareVO {

    public OrderWareVO(OrderInfo info, Long wareId) {
        if (info == null) {
            throw new NullPointerException("orderInfo is null !!!");
        }
        this.wareId = wareId;
        this.orderId = info.getId();
        this.consigneeTel = info.getConsigneeTel();
        this.orderComment = info.getOrderComment();
        this.orderBody = info.getTradeBody();
        this.consignee = info.getConsignee();
        this.deliveryAddress = info.getDeliveryAddress();
        this.paymentWay = info.getPaymentWay();
        this.paymentWay = this.paymentWay.equals(PaymentType.ALIPAY.name()) ? "2" : "1";
        if (info.getOrderDetailList() != null) {
            this.details = new ArrayList<>(info.getOrderDetailList().size());
            info.getOrderDetailList().stream().forEach(item -> {
                DetailVO detailVO = new DetailVO(item.getSkuId(), item.getSkuNum(), item.getSkuName());
                this.details.add(detailVO);
            });
        }
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

    private Long wareId;
    private Long orderId;
    private String consignee;
    private String consigneeTel;
    private String orderComment;
    private String orderBody;
    private String deliveryAddress;

    private String paymentWay;
    private List<DetailVO> details;

    @AllArgsConstructor
    @Getter
    private static class DetailVO {
        private Long skuId;
        private Integer skuNum;
        private String skuName;
    }
}
