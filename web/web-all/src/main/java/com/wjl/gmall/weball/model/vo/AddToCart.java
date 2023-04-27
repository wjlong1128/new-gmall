package com.wjl.gmall.weball.model.vo;

import lombok.Data;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/27
 * @description
 */
@Data
public class AddToCart {
    // 商品id
    private Long skuId;

    // 数量
    private Integer skuNum;

    // 资源类型
    private String sourceType;
}
