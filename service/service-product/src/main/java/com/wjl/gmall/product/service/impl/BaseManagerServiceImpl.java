package com.wjl.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wjl.gmall.common.cache.GmallCache;
import com.wjl.gmall.product.mapper.*;
import com.wjl.gmall.product.model.entity.*;
import com.wjl.gmall.product.model.vo.CategoryVO;
import com.wjl.gmall.product.service.BaseManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    /**
     * 获取任一分类级别下的平台属性
     *
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @Override
    public List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id) {
        return baseAttrInfoMapper.getAttrInfoList(category1Id, category2Id, category3Id);
    }

    /**
     * 保存或更新基本属性与基本属性值
     *
     * @param baseAttrInfo
     */
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

    /**
     * 根据基本属性id获取基本属性值集合
     *
     * @param attrId
     * @return
     */
    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        // 避免脏数据
        return getAttrInfoById(attrId).getAttrValueList();
    }

    /**
     * 根据基本属性id获取基本属性与基本属性值集合
     *
     * @param attrId
     * @return
     */
    @Override
    public BaseAttrInfo getAttrInfoById(Long attrId) {
        BaseAttrInfo attrInfo = baseAttrInfoMapper.selectById(attrId);
        List<BaseAttrValue> values = getAttrValueListByAttrId(attrInfo.getId());
        attrInfo.setAttrValueList(values);
        return attrInfo;
    }

    /**
     * 查询分类视图  手机 > 手机通讯 > 智能手机
     *
     * @param category3Id
     * @return
     */
    @GmallCache(prefix = "categoryView")
    @Override
    public BaseCategoryView getCategoryView(Long category3Id) {
        return categoryViewMapper.selectById(category3Id);
    }

    /**
     * 获取所有分类 以层级关系展示
     *
     * @return
     */
    @GmallCache(prefix = "getCategoryList")
    @Override
    public List<CategoryVO> getCategoryList() {
        ArrayList<CategoryVO> list = new ArrayList<>();

        List<BaseCategoryView> views = categoryViewMapper.selectList(null);
        // 视图查出所有，根据一级分类的id分组
        Map<Long, List<BaseCategoryView>> category1Map =
                views.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));


        int index = 1;
        for (Map.Entry<Long, List<BaseCategoryView>> entry : category1Map.entrySet()) {
            // key 一级分类id, value 一条数据
            //System.out.println("key:" + entry.getKey() + "   value:"+entry.getValue());
            CategoryVO categoryVO = new CategoryVO();
            String c1Name = entry.getValue().get(0).getCategory1Name();
            categoryVO.setCategoryName(c1Name);
            // index++
            categoryVO.setIndex(index++);
            categoryVO.setCategoryId(entry.getKey());
            ArrayList<CategoryVO.CategoryChild> category2Child = new ArrayList<>();
            // 获取二级分类
            Map<Long, List<BaseCategoryView>> category2Map = entry.getValue().stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            for (Map.Entry<Long, List<BaseCategoryView>> c2Entry : category2Map.entrySet()) {
                CategoryVO.CategoryChild e = new CategoryVO.CategoryChild();
                e.setCategoryId(c2Entry.getKey());
                e.setCategoryName(c2Entry.getValue().get(0).getCategory2Name());

                ArrayList<CategoryVO.CategoryChild> c2Child = new ArrayList<>();
                Set<Map.Entry<Long, List<BaseCategoryView>>> category3Map = c2Entry.getValue().stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory3Id)).entrySet();
                for (Map.Entry<Long, List<BaseCategoryView>> c3Entry : category3Map) {
                    c2Child.add(new CategoryVO.CategoryChild(c3Entry.getKey(),c3Entry.getValue().get(0).getCategory3Name(),null));
                }

                e.setCategoryChild(c2Child);

                category2Child.add(e);
            }
            categoryVO.setCategoryChild(category2Child);
            list.add(categoryVO);
        }
        return list;
    }


    private List<BaseAttrValue> getAttrValueListByAttrId(Long id) {
        LambdaQueryWrapper<BaseAttrValue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseAttrValue::getAttrId, id);
        return baseAttrValueMapper.selectList(queryWrapper);
    }


}

