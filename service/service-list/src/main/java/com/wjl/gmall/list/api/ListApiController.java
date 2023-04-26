package com.wjl.gmall.list.api;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.list.model.document.Goods;
import com.wjl.gmall.list.model.query.SearchParam;
import com.wjl.gmall.list.model.vo.SearchResponseVo;
import com.wjl.gmall.list.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/25
 * @description
 */
@RestController
@RequestMapping("api/list/inner")
public class ListApiController {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private SearchService searchService;


    /**
     * 商品上架
     * @param skuId
     * @return
     */
    @GetMapping("/upperGoods/{skuId}")
    public Result upperGoods(@PathVariable("skuId") Long skuId) {
        searchService.upperGoods(skuId);
        return Result.ok();
    }

    @GetMapping("/lowerGoods/{skuId}")
    public Result lowerGoods(@PathVariable("skuId") Long skuId) {
        searchService.lowerGoods(skuId);
        return Result.ok();
    }

    /**
     *  商品热度
     * @param skuId
     * @return
     */
    @GetMapping("/incrHotScore/{skuId}")
    public Result incrHotScore(@PathVariable("skuId") Long skuId){
        searchService.incrHotScore(skuId);
        return Result.ok();
    }

    /**
     * 创建索引库 简历mapping结构
     *
     * @return
     */
    @GetMapping("/createIndex")
    public Result createIndex() {
        // 过时了 这里方便就不讲究了
        elasticsearchRestTemplate.createIndex(Goods.class);
        boolean success = elasticsearchRestTemplate.putMapping(Goods.class);
        return Result.ok(success);
    }


    /**
     * 商品搜索接口
     * @param param
     * @return
     */
    @PostMapping("search")
    public Result<SearchResponseVo> search(@RequestBody SearchParam param){
        SearchResponseVo responseVo = searchService.search(param);
        return Result.ok(responseVo);
    }

}
