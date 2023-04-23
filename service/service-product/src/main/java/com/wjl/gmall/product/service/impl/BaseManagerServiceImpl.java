package com.wjl.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wjl.gmall.model.product.*;
import com.wjl.gmall.product.mapper.*;
import com.wjl.gmall.product.service.BaseManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/21
 * @description
 */

@Service
public class BaseManagerServiceImpl implements BaseManagerService {

    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;

    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;

    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    private CategoryViewMapper categoryViewMapper;

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Override
    public List<BaseCategory1> getCategory1() {
        List<BaseCategory1> list = baseCategory1Mapper.selectList(null);
        return list;
    }

    @Override
    public List<BaseCategory2> getCategory2List(Long id) {
        return baseCategory2Mapper
                .selectList(new LambdaQueryWrapper<BaseCategory2>().eq(BaseCategory2::getCategory1Id, id));
    }

    @Override
    public List<BaseCategory3> getCategory3List(Long id) {
        return baseCategory3Mapper.selectList(new LambdaQueryWrapper<BaseCategory3>().eq(BaseCategory3::getCategory2Id, id));
    }

    @Override
    public List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id) {
        return baseAttrInfoMapper.getAttrInfoList(category1Id, category2Id, category3Id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        Long baseAttrInfoId = baseAttrInfo.getId();
        if (baseAttrInfoId != null) {
            baseAttrInfoMapper.updateById(baseAttrInfo);
        } else {
            baseAttrInfoMapper.insert(baseAttrInfo);
        }
        List<BaseAttrValue> newValueList = baseAttrInfo.getAttrValueList();
        if (!CollectionUtils.isEmpty(newValueList)) {

            List<BaseAttrValue> valueList = baseAttrValueMapper.selectList(new LambdaQueryWrapper<BaseAttrValue>().eq(BaseAttrValue::getAttrId, baseAttrInfoId));

            if (!CollectionUtils.isEmpty(valueList)) {
                List<Long> ids = valueList.stream().map(BaseAttrValue::getId).collect(Collectors.toList());
                baseAttrValueMapper.deleteBatchIds(ids);
            }

            newValueList.forEach(item -> {
                item.setAttrId(baseAttrInfo.getId());
                // 这里写sql更好，但是懒
                baseAttrValueMapper.insert(item);
            });

        }

    }

    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        // 避免脏数据
        return getAttrInfoById(attrId).getAttrValueList();
    }

    @Override
    public BaseAttrInfo getAttrInfoById(Long attrId) {
        BaseAttrInfo attrInfo = baseAttrInfoMapper.selectById(attrId);
        List<BaseAttrValue> values = getAttrValueListByAttrId(attrInfo.getId());
        attrInfo.setAttrValueList(values);
        return attrInfo;
    }

    @Override
    public BaseCategoryView getCategoryView(Long category3Id) {
        return categoryViewMapper.selectById(category3Id);
    }




    private List<BaseAttrValue> getAttrValueListByAttrId(Long id) {
        LambdaQueryWrapper<BaseAttrValue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseAttrValue::getAttrId, id);
        return baseAttrValueMapper.selectList(queryWrapper);
    }


}

