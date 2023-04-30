package com.wjl.gmall.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.gmall.model.enums.PaymentStatus;
import com.wjl.gmall.model.enums.PaymentType;
import com.wjl.gmall.order.model.dto.OrderInfo;
import com.wjl.gmall.pay.mapper.PaymentInfoMapper;
import com.wjl.gmall.pay.model.entity.PaymentInfo;
import com.wjl.gmall.pay.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/30
 * @description
 */
@Service
public class PaymentServiceimpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentService {

    /**
     *  添加支付中
     * @param orderUnpaid
     * @param paymentType
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveUnpaidPaymentInfo(OrderInfo orderUnpaid, PaymentType paymentType) {
        LambdaQueryWrapper<PaymentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentInfo::getPaymentType, paymentType.name());
        queryWrapper.eq(PaymentInfo::getOrderId,orderUnpaid.getId());
        int count = this.count(queryWrapper);
        if (count > 0){
            throw new RuntimeException("订单已经支付");
        }
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentType(paymentType.name());
        paymentInfo.setOrderId(orderUnpaid.getId());
        paymentInfo.setOutTradeNo(orderUnpaid.getOutTradeNo());
        paymentInfo.setUserId(orderUnpaid.getUserId());
        paymentInfo.setTotalAmount(orderUnpaid.getTotalAmount());
        paymentInfo.setSubject(orderUnpaid.getTradeBody());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID.name());
        this.save(paymentInfo);
    }
}
