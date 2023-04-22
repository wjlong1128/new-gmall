package com.wjl.gmall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjl.gmall.model.product.BaseAttrInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/21
 * @description
 */
//@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {
    List<BaseAttrInfo> getAttrInfoList(
            @Param("category1Id") Long category1Id,
            @Param("category2Id") Long category2Id,
            @Param("category3Id") Long category3Id
    );
}