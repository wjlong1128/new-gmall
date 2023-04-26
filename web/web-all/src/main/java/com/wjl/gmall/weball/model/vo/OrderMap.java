package com.wjl.gmall.weball.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/26
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderMap {
    // 排序类型 hotSort
    private String type;
    // 排序方式 asc
    private String sort;
}
