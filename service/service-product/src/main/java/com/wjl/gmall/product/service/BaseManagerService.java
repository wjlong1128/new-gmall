package com.wjl.gmall.product.service;

import com.wjl.gmall.product.model.entity.*;
import com.wjl.gmall.product.model.vo.CategoryVO;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/21
 * @description
 */
public interface BaseManagerService {
    List<BaseCategory1> getCategory1();

    List<BaseCategory2> getCategory2List(Long id);

    List<BaseCategory3> getCategory3List(Long id);

    /**
     *  获取任一分类级别下的平台属性
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id);

    /**
     *  保存或更新基本属性与基本属性值
     * @param baseAttrInfo
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     *  根据基本属性id获取基本属性值集合
     * @param attrId
     * @return
     */
    List<BaseAttrValue> getAttrValueList(Long attrId);

    /**
     *  根据基本属性id获取基本属性与基本属性值集合
     * @param attrId
     * @return
     */
    BaseAttrInfo getAttrInfoById(Long attrId);

    /**
     *  查询分类视图  手机 > 手机通讯 > 智能手机
     * @param category3Id
     * @return
     */

    BaseCategoryView getCategoryView(Long category3Id);


    /**
     *  获取所有分类 以层级关系展示
     * @return
     */
    List<CategoryVO> getCategoryList();
}
