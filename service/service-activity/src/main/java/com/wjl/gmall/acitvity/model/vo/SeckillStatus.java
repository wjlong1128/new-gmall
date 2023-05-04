package com.wjl.gmall.acitvity.model.vo;

import com.wjl.gmall.common.result.ResultCodeEnum;
import lombok.Data;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/4
 * @description
 */
@Data
public class SeckillStatus {
    private ResultCodeEnum status;
    private Object data;
}
