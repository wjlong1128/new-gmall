package com.wjl.gmall.acitvity.model.vo;

import com.wjl.gmall.order.model.dto.OrderDetail;
import com.wjl.gmall.user.dto.UserAddress;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/4
 * @description
 */
@Data
public class SeckillTradeVo {
    private Integer totalNum;
    private BigDecimal totalAmount;
    private List<OrderDetail> detailArrayList;
    private List<UserAddress> userAddressList;
}
