package com.wjl.gmall.order.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/2
 * @description
 */
@Data
public class WareSkuVo {
    private String wareId;
    private List<String> skuIds;
}
