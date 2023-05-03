package com.wjl.gmall.pay.api;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.common.util.AuthContextHolder;
import com.wjl.gmall.model.enums.PaymentType;
import com.wjl.gmall.pay.config.properites.AlipayProperties;
import com.wjl.gmall.pay.model.entity.PaymentInfo;
import com.wjl.gmall.pay.service.AliPayService;
import com.wjl.gmall.pay.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/30
 * @description
 */
@RequestMapping("api/payment")
@Controller
public class PaymentController {

    @Autowired
    private AliPayService payService;

    @Autowired
    private PaymentService paymentService;

    /**
     * 付款页面
     *
     * @param orderId
     * @return
     */
    @ResponseBody
    @GetMapping("/alipay/submit/{orderId}")
    public String submitOrder(@PathVariable("orderId") Long orderId) {
        String alipayFromPage = payService.submitOrder(orderId);
        return alipayFromPage;
    }

    /**
     * 支付成功同步回调接口
     */
    @RequestMapping("/alipay/callback/return")
    public String syncCallback() {
        return "redirect:" + AlipayProperties.RETURN_ORDER_URL;
    }

    /**
     * 支付成功异步回调接口
     *
     * @param paramsMap
     * @return
     */
    @ResponseBody
    @PostMapping("/alipay/callback/notify")
    public String notifyCallback(@RequestParam Map<String, String> paramsMap) {
        String returnString = payService.verifySign(paramsMap);
        return returnString;
    }

    /**
     * 退款接口
     *
     * @param orderId
     * @param request
     * @return
     */
    @ResponseBody
    @GetMapping("alipay/refund/{orderId}")
    public Result refund(@PathVariable("orderId") Long orderId, HttpServletRequest request) {
        String userId = AuthContextHolder.getUserId(request);
        this.payService.refund(orderId, userId);
        return Result.ok();
    }


    /**
     * 查询支付宝交易记录
     *
     * @param orderId
     * @return
     */
    @ResponseBody
    @GetMapping("alipay/checkPayment/{orderId}")
    public Result<Boolean> checkPayment(@PathVariable("orderId") Long orderId) {
        boolean exists = payService.checkPayment(orderId);
        return Result.ok(exists);
    }


    /**
     * 支付宝关闭交易 用户已经扫码 但是未支付
     *
     * @param orderId
     * @return
     */
    @ResponseBody
    @GetMapping("alipay/closePay/{orderId}")
    public Result<Boolean> closePay(@PathVariable("orderId") Long orderId) {
        boolean success = this.payService.closePay(orderId);
        return Result.ok(success);
    }

    /**
     * 查询是否有支付记录
     *
     * @param orderId
     * @return
     */
    @ResponseBody
    @GetMapping("getPaymentInfo/{orderId}/{type}")
    public Result<PaymentInfo> getPaymentInfo(@PathVariable("orderId") Long orderId, @PathVariable("type") String type) {
        PaymentInfo paymentInfo = this.paymentService.getPaymentInfo(orderId, PaymentType.valueOf(type));
        return Result.ok(paymentInfo);
    }

    /**
     * 查询是否有支付记录 无论支付方式
     *
     * @param orderId
     * @return
     */
    @ResponseBody
    @GetMapping("getPaymentInfo/{orderId}")
    public Result<PaymentInfo> getPaymentInfo(@PathVariable("orderId") Long orderId) {
        PaymentInfo paymentInfo = this.paymentService.getPaymentInfo(orderId);
        return Result.ok(paymentInfo);
    }
}
