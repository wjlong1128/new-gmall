package com.wjl.gmall.pay.api;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.common.util.AuthContextHolder;
import com.wjl.gmall.pay.config.properites.AlipayProperties;
import com.wjl.gmall.pay.service.AliPayService;
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

    @ResponseBody
    @GetMapping("alipay/refund/{orderId}")
    public Result refund(@PathVariable("orderId") Long orderId, HttpServletRequest request) {
        String userId = AuthContextHolder.getUserId(request);
        this.payService.refund(orderId, userId);
        return Result.ok();
    }
}
