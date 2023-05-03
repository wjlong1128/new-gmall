package com.wjl.gmall.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.gmall.common.constants.MqConst;
import com.wjl.gmall.common.service.RabbitService;
import com.wjl.gmall.model.enums.PaymentStatus;
import com.wjl.gmall.model.enums.PaymentType;
import com.wjl.gmall.order.model.dto.OrderInfo;
import com.wjl.gmall.pay.mapper.PaymentInfoMapper;
import com.wjl.gmall.pay.model.entity.PaymentInfo;
import com.wjl.gmall.pay.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.Optional;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/30
 * @description
 */
@Service
public class PaymentServiceimpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentService {

    @Autowired
    private RabbitService rabbitService;

    /**
     * 添加支付中
     *
     * @param orderUnpaid
     * @param paymentType
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveUnpaidPaymentInfo(OrderInfo orderUnpaid, PaymentType paymentType) {
        LambdaQueryWrapper<PaymentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentInfo::getPaymentType, paymentType.name());
        queryWrapper.eq(PaymentInfo::getOrderId, orderUnpaid.getId());
        int count = this.count(queryWrapper);
        if (count > 0) {
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

    @Override
    public Optional<PaymentInfo> getPaymentInfoWithOutTradeNo(String outTradeNo, PaymentType type) {
        LambdaQueryWrapper<PaymentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentInfo::getOutTradeNo, outTradeNo).eq(PaymentInfo::getPaymentType, type.name());
        PaymentInfo info = this.getOne(queryWrapper);
        return Optional.ofNullable(info);
    }

    /**
     * @param outTradeNo 订单和支付宝唯一的号
     * @param type       支付类型
     * @param tradeNo    支付宝订单号
     * @param status     订单状态
     */
    @Transactional
    @Override
    public void updatePaymentInfo(String outTradeNo, PaymentType type, @Nullable String tradeNo, PaymentStatus status) {
        LambdaQueryWrapper<PaymentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentInfo::getOutTradeNo, outTradeNo);
        PaymentInfo entity = new PaymentInfo();
        entity.setOutTradeNo(outTradeNo);
        entity.setTradeNo(tradeNo);
        entity.setPaymentType(type.name());
        entity.setCallbackTime(new Date());
        entity.setPaymentStatus(status.name());
        boolean update = this.update(entity, wrapper);
        if (!update) {
            throw new RuntimeException("修改状态失败");
        }
        PaymentInfo newPayment = this.getOne(wrapper);
        // 支付成功
        if (status.name().equals(PaymentStatus.PAID.name())) {
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_PAYMENT_PAY, MqConst.ROUTING_PAYMENT_PAY, newPayment.getOrderId());
        }

        // 退款成功
        if (status.name().equals(PaymentStatus.CLOSED.name())) {
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_PAYMENT_CLOSE, MqConst.ROUTING_PAYMENT_CLOSE, newPayment.getOrderId());
        }
    }


    /**
     * 必须是未支付 且指定付款方式的才更新为关闭状态
     *
     * @param orderId
     * @param type
     */
    @Override
    public void closePayment(Long orderId, PaymentType type) {
        LambdaQueryWrapper<PaymentInfo> updateWrapper = new LambdaQueryWrapper<>();
        PaymentInfo entity = new PaymentInfo();
        entity.setPaymentStatus(PaymentStatus.CLOSED.name());
        updateWrapper
                .eq(PaymentInfo::getOrderId, orderId)
                .eq(PaymentInfo::getPaymentType, type.name())
                .eq(PaymentInfo::getPaymentStatus, PaymentStatus.UNPAID)
        ;
        this.update(entity, updateWrapper);
    }

    @Override
    public Optional<PaymentInfo> getPaymentInfoWithOrderId(Long orderId) {
        LambdaQueryWrapper<PaymentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentInfo::getOrderId, orderId);
        PaymentInfo info = this.getOne(queryWrapper);
        return Optional.ofNullable(info);
    }

    @Override
    public PaymentInfo getPaymentInfo(Long orderId) {
        Optional<PaymentInfo> paymentInfoWithOrderId = this.getPaymentInfoWithOrderId(orderId);
        if (paymentInfoWithOrderId.isPresent()) {
            return paymentInfoWithOrderId.get();
        }
        return null;
    }

    @Override
    public PaymentInfo getPaymentInfo(Long orderId, PaymentType type) {
        LambdaQueryWrapper<PaymentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentInfo::getOrderId, orderId);
        queryWrapper.eq(PaymentInfo::getPaymentType, type.name());
        return this.getOne(queryWrapper);
    }
}
