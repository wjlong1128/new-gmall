package com.wjl.gmall.list.client;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.list.client.impl.ListDegradeServiceClientFallFactory;
import com.wjl.gmall.list.model.query.SearchParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/25
 * @description
 */
@FeignClient(value = "service-list", fallbackFactory = ListDegradeServiceClientFallFactory.class)
public interface ListServiceClient {

    /**
     * 商品上架
     *
     * @param skuId
     * @return
     */
    @GetMapping("api/list/inner/upperGoods/{skuId}")
    public Result upperGoods(@PathVariable("skuId") Long skuId);

    @GetMapping("api/list/inner/lowerGoods/{skuId}")
    public Result lowerGoods(@PathVariable("skuId") Long skuId);

    /**
     * 商品热度
     *
     * @param skuId
     * @return
     */
    @GetMapping("api/list/inner/incrHotScore/{skuId}")
    public Result incrHotScore(@PathVariable("skuId") Long skuId);

    /**
     * 创建索引库 简历mapping结构
     *
     * @return
     */
    @GetMapping("api/list/inner/createIndex")
    public Result createIndex();


    /**
     * 商品搜索接口
     * @param param
     * @return
     */
    @PostMapping("api/list/inner/search")
    public Result<Map<String,Object>> search(SearchParam param);
}
