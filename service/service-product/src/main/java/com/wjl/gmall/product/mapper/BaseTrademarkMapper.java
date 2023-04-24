package com.wjl.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjl.gmall.product.model.entity.BaseTrademark;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
public interface BaseTrademarkMapper extends BaseMapper<BaseTrademark> {
    /**
     *  根据id批量获取
     * @param ids
     * @return
     */
    List<BaseTrademark> selectTrademarkByIds(@Param("ids") List<Long> ids);
}
