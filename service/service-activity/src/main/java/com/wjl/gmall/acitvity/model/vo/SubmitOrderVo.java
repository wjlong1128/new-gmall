package com.wjl.gmall.acitvity.model.vo;

import com.wjl.gmall.order.model.dto.OrderInfo;
import lombok.Data;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/4
 * @description
 */
@Data
public class SubmitOrderVo {
    private Long skuId;
    private OrderInfo order;
}
