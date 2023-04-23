package com.wjl.gmall.product.service;

import com.wjl.gmall.model.product.*;

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

    List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    List<BaseAttrValue> getAttrValueList(Long attrId);

    BaseAttrInfo getAttrInfoById(Long attrId);


    BaseCategoryView getCategoryView(Long category3Id);


}
