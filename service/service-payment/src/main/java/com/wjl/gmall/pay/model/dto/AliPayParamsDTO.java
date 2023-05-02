package com.wjl.gmall.pay.model.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/1
 * @description
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AliPayParamsDTO {

    public static String requestParamsJson(String out_trade_no, String total_amount, String subject, String product_code, String timeout_express) {
        if ((out_trade_no != null && total_amount != null && Double.parseDouble(total_amount) > 0 && subject != null && timeout_express != null)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("out_trade_no", out_trade_no);
            jsonObject.put("total_amount", new BigDecimal(total_amount));
            jsonObject.put("subject", subject);
            jsonObject.put("product_code", product_code);
            return jsonObject.toJSONString();
        }

        throw new NullPointerException("参数不能为空");
    }

    public static String requestRefundJson(String trade_no, String refund_amount, String out_request_no) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("out_trade_no", trade_no);
        jsonObject.put("out_request_no", out_request_no);
        jsonObject.put("refund_amount", new BigDecimal(refund_amount));
        return jsonObject.toJSONString();
    }
}
