package com.wjl.gmall.list.service.impl;

import com.alibaba.fastjson.JSON;
import com.wjl.gmall.list.model.document.Goods;
import com.wjl.gmall.list.model.document.SearchAttr;
import com.wjl.gmall.list.model.query.SearchParam;
import com.wjl.gmall.list.model.vo.SearchResponseAttrVo;
import com.wjl.gmall.list.model.vo.SearchResponseTmVo;
import com.wjl.gmall.list.model.vo.SearchResponseVo;
import com.wjl.gmall.list.repository.GoodsRepository;
import com.wjl.gmall.list.service.SearchService;
import com.wjl.gmall.product.client.ProductServiceClient;
import com.wjl.gmall.product.client.model.dto.BaseAttrInfo;
import com.wjl.gmall.product.client.model.dto.BaseCategoryView;
import com.wjl.gmall.product.client.model.dto.BaseTrademark;
import com.wjl.gmall.product.client.model.dto.SkuInfo;
import lombok.SneakyThrows;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/25
 * @description
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ProductServiceClient productServiceClient;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 商品上架
     *
     * @param skuId
     */
    @Override
    public void upperGoods(Long skuId) {
        SkuInfo skuInfo = productServiceClient.getSkuInfoAndImages(skuId).getData();
        if (skuInfo == null || skuInfo.getSpuId() == null) return;

        Goods goods = new Goods();
        // 基本信息
        goods.setId(skuId);
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setTitle(skuInfo.getSkuName());
        BigDecimal price = productServiceClient.getSkuPrice(skuId).getData();
        goods.setPrice(price.doubleValue());
        goods.setCreateTime(new Date());
        goods.setTmId(skuInfo.getTmId());
        // 品牌信息
        CompletableFuture<Void> trademarkFuture = CompletableFuture.runAsync(() -> {
            BaseTrademark trademark = productServiceClient.getTrademark(skuInfo.getTmId()).getData();
            if (trademark != null) {
                goods.setTmName(trademark.getTmName());
                goods.setTmLogoUrl(trademark.getLogoUrl());
            }
        }, executor);
        // 分类信息
        CompletableFuture<Void> categoryFuture = CompletableFuture.runAsync(() -> {
            BaseCategoryView view = productServiceClient.getCategoryView(skuInfo.getCategory3Id()).getData();
            if (view != null) {
                goods.setCategory1Id(view.getCategory1Id());
                goods.setCategory1Name(view.getCategory1Name());
                goods.setCategory2Id(view.getCategory2Id());
                goods.setCategory2Name(view.getCategory2Name());
                goods.setCategory3Id(view.getCategory3Id());
                goods.setCategory3Name(goods.getCategory3Name());
            }
        }, executor);

        CompletableFuture<Void> attrListFuture = CompletableFuture.runAsync(() -> {
            // 平台属性
            List<BaseAttrInfo> attrList = productServiceClient.getAttrList(skuId).getData();
            if (!CollectionUtils.isEmpty(attrList)) {
                List<SearchAttr> searchAttrList = attrList.stream().map(attr -> {
                    SearchAttr searchAttr = new SearchAttr();
                    searchAttr.setAttrId(attr.getId());
                    searchAttr.setAttrName(attr.getAttrName());
                    // 由于这个接口是根据skuid查出的具体的基本属性 也就是说一个基本属性对应一个基本属性值，一个手机系统只能对应安卓系统
                    searchAttr.setAttrValue(attr.getAttrValueList().get(0).getValueName());
                    return searchAttr;
                }).collect(Collectors.toList());
                goods.setAttrs(searchAttrList);
            }
        }, executor);

        CompletableFuture.allOf(trademarkFuture, categoryFuture, attrListFuture).join();
        goodsRepository.save(goods);
    }

    /**
     * 下架
     *
     * @param skuId
     */
    @Override
    public void lowerGoods(Long skuId) {
        goodsRepository.deleteById(skuId);
    }

    /**
     * 商品热度增加
     *
     * @param skuId
     */
    @Override
    public void incrHotScore(Long skuId) {
        String key = "hotScore";
        String field = "sku:" + skuId;
        Double score = redisTemplate.opsForZSet().incrementScore(key, field, 1);
        // 每十次更新es
        if (score.intValue() % 10 == 0) {
            // TODO 上架逻辑还没写
            try {
                Optional<Goods> optionalGoods = goodsRepository.findById(skuId);
                if (optionalGoods.isPresent()) {
                    Goods goods = optionalGoods.get();
                    // 四舍五入
                    goods.setHotScore(Math.round(score.doubleValue()));
                    goodsRepository.save(goods);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 商品搜索
     *
     * @param param
     * @return POST http://localhost:8203/api/list/inner/search?category3Id=61&trademark=2:华为&props=23:4G:运行内存&order=1:desc
     */
    @Override
    @SneakyThrows
    public SearchResponseVo search(SearchParam param) {
        SearchRequest request = this.buildQuery(param);
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        SearchResponseVo responseVo = this.parseSearchResponse(response);
        responseVo.setPageNo(param.getPageNo());
        responseVo.setPageSize(param.getPageSize());
        //
        // 总页8 每页 3   (8 + 3 - 1) / 3 = 3 页  计算总页数公式
        Long totalPages = (responseVo.getTotal() + responseVo.getPageSize() - 1)  / responseVo.getPageSize();
        responseVo.setTotalPages(totalPages);
        return responseVo;
    }


    /**
     * 封装查询条件
     *
     * @param param
     * @return
     */
    private SearchRequest buildQuery(SearchParam param) {
        SearchRequest request = new SearchRequest("goods");

        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (StringUtils.hasText(param.getKeyword())) {
            // title:{ "query": "小米手机", "operator": "and"}
            MatchQueryBuilder queryTitle = QueryBuilders
                    .matchQuery("title", param.getKeyword())
                    .operator(Operator.AND);
            query.must(queryTitle);
        }

        // 品牌 trademark=2:华为
        String trademark = param.getTrademark();
        if (StringUtils.hasText(trademark)) {
            String[] split = trademark.split(":");
            if (split.length == 2) {
                String tId = split[0];
                //    String tName = split[1];
                query.filter(QueryBuilders.termQuery("tmId", tId));
            }
        }

        // 分类
        if (param.getCategory1Id() != null) {
            query.filter(QueryBuilders.termsQuery("category1Id", param.getCategory1Id().toString()));
        }
        if (param.getCategory2Id() != null) {
            query.filter(QueryBuilders.termsQuery("category2Id", param.getCategory2Id().toString()));
        }
        if (param.getCategory3Id() != null) {
            query.filter(QueryBuilders.termsQuery("category3Id", param.getCategory3Id().toString()));
        }

        // 平台属性 props=23:4G:运行内存  1.平台属性id  2.平台属性值  3.平台属性名
        String[] props = param.getProps();
        if (props != null && props.length > 0) {
            for (String prop : props) {
                String[] split = prop.split(":");
                if (split.length == 3) {
                    String attrId = split[0];
                    String attrValue = split[1];
                    String attrName = split[2];
                    // 创建多条件对象
                    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                    BoolQueryBuilder subBoolQuery = QueryBuilders.boolQuery();
                    subBoolQuery.must(QueryBuilders.termsQuery("attrs.attrId", attrId));
                    subBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValue));
                    NestedQueryBuilder attrsNestedQuery = QueryBuilders.nestedQuery("attrs", subBoolQuery, ScoreMode.None);
                    boolQuery.must(attrsNestedQuery);
                    // 添加到总条件中
                    query.filter(boolQuery);
                }
            }
        }
        SearchSourceBuilder sourceBuilder = request.source();
        sourceBuilder.query(query);

        Integer pageNo = param.getPageNo() != null ? param.getPageNo() : 1;
        Integer pageSize = param.getPageSize();
        // 分页 与 排序
        sourceBuilder
                .from((pageNo - 1) * pageNo)
                .size(pageSize);

        // order=1:desc   1:hotScore  2:price
        String orderStr = param.getOrder();
        if (StringUtils.isEmpty(orderStr)) {
            orderStr = "1:desc"; // 没有默认按照热度最高的排序
        }
        String[] split = orderStr.split(":");
        String field = null;
        switch (split[0]) {
            case "1":
                field = "hotScore";
                break;
            case "2":
                field = "price";
                break;
            default:
                field = "hotScore";
        }
        SortOrder order = split[1].equals("asc") ? SortOrder.ASC : SortOrder.DESC;
        sourceBuilder.sort(field, order);

        // 高亮
        sourceBuilder.highlighter(new HighlightBuilder().field("title").preTags("<span style=color:red>").postTags("</span>"));

        // 聚合
        // 1.品牌聚合
        TermsAggregationBuilder tmId_agg = AggregationBuilders.terms("tmIdAgg").field("tmId").size(30);
        // 两个子
        TermsAggregationBuilder tmName_agg = AggregationBuilders.terms("tmNameAgg").field("tmName").size(30);
        TermsAggregationBuilder tmLogoUrl_agg = AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl").size(30);
        tmId_agg.subAggregation(tmName_agg);
        tmId_agg.subAggregation(tmLogoUrl_agg);
        sourceBuilder.aggregation(tmId_agg);

        // 2. 平台属性属性聚合
        NestedAggregationBuilder nested_agg = AggregationBuilders.nested("attrAgg", "attrs");
        // 子聚合
        TermsAggregationBuilder attrId_agg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId").size(30);
        TermsAggregationBuilder attrName_agg = AggregationBuilders.terms("attrNameAgg").field("attrs.attrName").size(30);
        attrId_agg.subAggregation(attrName_agg);
        TermsAggregationBuilder attrValue_agg = AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue").size(30);
        attrId_agg.subAggregation(attrValue_agg);
        // 子聚合的两个聚合
        nested_agg.subAggregation(attrId_agg);

        sourceBuilder.aggregation(nested_agg);

        // 指定要搜索的字段         参数1 要搜索的字段， 参数2 要排除的字段
        sourceBuilder.fetchSource(new String[]{"id", "defaultImg", "title", "price"}, new String[]{});
        System.out.println(sourceBuilder.toString());
        // Fuck
        // request.source(sourceBuilder);
        return request;
    }


    private SearchResponseVo parseSearchResponse(SearchResponse response) {
        SearchResponseVo result = new SearchResponseVo();
        // 总条数
        result.setTotal(response.getHits().getTotalHits().value);
        List<Goods> goodsList = new ArrayList<>();
        // 结果集
        for (SearchHit hit : response.getHits().getHits()) {
            Goods goods = JSON.parseObject(hit.getSourceAsString(), Goods.class);
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (!CollectionUtils.isEmpty(highlightFields)) {
                HighlightField highlightField = highlightFields.get("title");
                if (highlightField != null) {
                    String title = highlightField.getFragments()[0].toString();
                    goods.setTitle(title);
                }
            }
            goodsList.add(goods);
        }
        result.setGoodsList(goodsList);

        ArrayList<SearchResponseTmVo> trademarkList = new ArrayList<>();

        result.setTrademarkList(trademarkList);
        ParsedLongTerms tmIdAgg = response.getAggregations().get("tmIdAgg");
        for (Terms.Bucket bucket : tmIdAgg.getBuckets()) {
            SearchResponseTmVo e = new SearchResponseTmVo();
            // 品牌id
            e.setTmId(bucket.getKeyAsNumber().longValue());
            ParsedStringTerms tmNameAgg = bucket.getAggregations().get("tmNameAgg");
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
            // 品牌名称
            e.setTmName(tmName);
            ParsedStringTerms tmLogoUrlAgg = bucket.getAggregations().get("tmLogoUrlAgg");
            String tmLogoUrl = tmLogoUrlAgg.getBuckets().get(0).getKeyAsString();
            // 品牌URL
            e.setTmLogoUrl(tmLogoUrl);
            trademarkList.add(e);
        }

        ArrayList<SearchResponseAttrVo> attrsList = new ArrayList<>();
        // 平台属性
        ParsedNested attrAgg = response.getAggregations().get("attrAgg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            SearchResponseAttrVo e = new SearchResponseAttrVo();
            e.setAttrId(bucket.getKeyAsNumber().longValue());// 平台属性id

            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attrNameAgg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            e.setAttrName(attrName); // 平台属性名
            // 平台属性值
            ArrayList<String> attrValueList = new ArrayList<>();
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attrValueAgg");
            for (Terms.Bucket valueAggBucket : attrValueAgg.getBuckets()) {
                String value = valueAggBucket.getKeyAsString();
                attrValueList.add(value);
            }
            e.setAttrValueList(attrValueList);
            attrsList.add(e);
        }
        result.setAttrsList(attrsList);
        return result;
    }
}
