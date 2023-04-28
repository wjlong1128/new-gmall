package com.wjl.gmall.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wjl.gmall.order.model.entity.OrderInfo;
import org.apache.ibatis.annotations.Param;

/**
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/28
 * @description
 */
//@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
    IPage<OrderInfo> getOrdersPage(IPage<OrderInfo> page, @Param("userId")String userId);
}
