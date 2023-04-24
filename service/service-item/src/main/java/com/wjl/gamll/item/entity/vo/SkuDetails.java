package com.wjl.gamll.item.entity.vo;

import com.wjl.gmall.product.client.model.dto.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
@Data
public class SkuDetails {

    private BaseCategoryView categoryView;
    private List<BaseAttrInfo> skuAttrList;
    private SkuInfo skuInfo;

    private BigDecimal price;
    private List<SpuPoster> spuPosterList;

    private List<SpuSaleAttr> spuSaleAttrList;


    private Map valuesSkuJson;


}
