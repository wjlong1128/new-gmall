package com.wjl.gmall.list.model.document;

import lombok.Data;

import java.util.Date;
import java.util.List;

// Index = goods , Type = info  es 7.8.0 逐渐淡化type！  修改！
@Data
/**
 *  indexName 索引库名称
 *  shards 一条文档分成几份  10 -> 3 3 4
 *  replicas 副本数量 2
 */
public class Goods {
    // 商品Id skuId
    private Long id;

    private String defaultImg;

    //  es 中能分词的字段，这个字段数据类型必须是 text！keyword 不分词！
    private String title;

    private Double price;

    //  @Field(type = FieldType.Date)   6.8.1
    private Date createTime; // 新品

    private Long tmId;

    private String tmName;

    private String tmLogoUrl;

    private Long category1Id;

    private String category1Name;

    private Long category2Id;

    private String category2Name;

    private Long category3Id;

    private String category3Name;

    //  商品的热度！ 我们将商品被用户点查看的次数越多，则说明热度就越高！
    private Long hotScore = 0L;

    // 平台属性集合对象
    // Nested 支持嵌套查询
    private List<SearchAttr> attrs;

}
