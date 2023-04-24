package com.wjl.gmall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.gmall.model.product.SkuAttrValue;
import com.wjl.gmall.model.product.SkuImage;
import com.wjl.gmall.model.product.SkuInfo;
import com.wjl.gmall.model.product.SkuSaleAttrValue;
import com.wjl.gmall.product.mapper.SkuAttrValueMapper;
import com.wjl.gmall.product.mapper.SkuImageMapper;
import com.wjl.gmall.product.mapper.SkuInfoMapper;
import com.wjl.gmall.product.mapper.SkuSaleAttrValueMapper;
import com.wjl.gmall.product.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
@Service
public class SkuServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuService {

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private SkuAttrValueMapper attrValueMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;


    /**
     *  保存一个sku（商品）信息及其所关联的信息
     * @param skuInfo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSku(SkuInfo skuInfo) {
        skuInfo.setIsSale(0);
        List<SkuImage> images = skuInfo.getSkuImageList();
        images.forEach(image -> {
            if (image.getIsDefault().equals("1")) {
                skuInfo.setSkuDefaultImg(image.getImgUrl());
            }
        });

        skuInfoMapper.insert(skuInfo);
        images.forEach(image -> {
            image.setSkuId(skuInfo.getId());
            // 循环操作数据库...
            skuImageMapper.insert(image);
        });

        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (!CollectionUtils.isEmpty(skuAttrValueList)) {
            skuAttrValueList.forEach(attrValue -> {
                attrValue.setSkuId(skuInfo.getId());
                attrValueMapper.insert(attrValue);
            });
        }

        List<SkuSaleAttrValue> saleAttrValues = skuInfo.getSkuSaleAttrValueList();
        if (!CollectionUtils.isEmpty(saleAttrValues)) {
            saleAttrValues.forEach(s -> {
                s.setSkuId(skuInfo.getId());
                s.setSpuId(skuInfo.getSpuId());
                skuSaleAttrValueMapper.insert(s);
            });
        }
    }

    /**
     * 上架
     *
     * @param skuId
     */
    @Override
    public void onSale(Long skuId) {
        SkuInfo info = new SkuInfo();
        info.setIsSale(1);
        info.setId(skuId);
        updateById(info);
    }

    /**
     * 下架
     *
     * @param skuId
     */
    @Override
    public void cancelSale(Long skuId) {
        SkuInfo info = new SkuInfo();
        info.setIsSale(0);
        info.setId(skuId);
        updateById(info);
    }

    /**
     *  根据sku获取所属sku的基本信息以及sku图片信息
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfoAndImages(Long skuId) {
        SkuInfo skuInfo = this.getById(skuId);
        if (skuInfo == null){
            return null;
        }

        Long skuInfoId = skuInfo.getId();
        List<SkuImage> skuImages = skuImageMapper.getSkuImages(skuInfoId);
        skuInfo.setSkuImageList(skuImages);

        return skuInfo;
    }

    /**
     *  获取sku实时价格
     * @param skuId
     * @return
     */
    @Override
    public BigDecimal getSkuPrice(Long skuId) {
        return this.baseMapper.getPrice(skuId);
    }
}
