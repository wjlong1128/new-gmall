package com.wjl.gmall.list.service;

import com.wjl.gmall.list.model.query.SearchParam;
import com.wjl.gmall.list.model.vo.SearchResponseVo;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/25
 * @description
 */
public interface SearchService {

    /**
     *  商品上架
     * @param skuId
     */
    void upperGoods(Long skuId);

    void lowerGoods(Long skuId);

    /**
     *  商品热度增加
     * @param skuId
     */
    void incrHotScore(Long skuId);


    /**
     *  商品搜索
     * @param param
     * @return
     */
    SearchResponseVo search(SearchParam param);
}
