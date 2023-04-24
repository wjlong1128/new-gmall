package com.wjl.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wjl.gmall.model.product.*;
import com.wjl.gmall.product.mapper.*;
import com.wjl.gmall.product.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
@Service
public class SpuServiceImpl implements SpuService {
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    private SpuImagesMapper spuImagesValueMapper;

    @Autowired
    private SpuPosterMapper spuPosterMapper;
    @Autowired
    private SpuInfoMapper spuInfoMapper;


    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Override
    public Page<SpuInfo> getSpuInfoList(Long page, Long limit, SpuInfo spuInfo) {
        LambdaQueryWrapper<SpuInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(spuInfo.getCategory3Id() != null, SpuInfo::getCategory3Id, spuInfo.getCategory3Id());
        Page<SpuInfo> infoPage = spuInfoMapper.selectPage(new Page<>(page, limit), wrapper);
        return infoPage;
    }

    /**
     * 保存spu基本信息以及关联到的一个spu所拥有的所有信息
     * @param spuInfo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        spuInfoMapper.insert(spuInfo);
        Long spuId = spuInfo.getId();

        List<SpuImage> imageList = spuInfo.getSpuImageList();
        if (!CollectionUtils.isEmpty(imageList)) {
            imageList.forEach(image -> {
                image.setSpuId(spuId);
                spuImagesValueMapper.insert(image);
            });
        }

        List<SpuPoster> posterList = spuInfo.getSpuPosterList();
        if (!CollectionUtils.isEmpty(posterList)) {
            posterList.forEach(poster -> {
                poster.setSpuId(spuId);
                spuPosterMapper.insert(poster);
            });
        }

        // 保存属性
        List<SpuSaleAttr> saleAttrList = spuInfo.getSpuSaleAttrList();
        if (CollectionUtils.isEmpty(saleAttrList)) {
            return;
        }

        for (SpuSaleAttr saleAttr : saleAttrList) {
            saleAttr.setSpuId(spuId);
            spuSaleAttrMapper.insert(saleAttr);
            List<SpuSaleAttrValue> values = saleAttr.getSpuSaleAttrValueList();
            if (CollectionUtils.isEmpty(values)) {
                continue;
            }
            // Long saleAttrId = saleAttr.getId();
            values.forEach(value -> {
                value.setSpuId(spuId);
                value.setBaseSaleAttrId(saleAttr.getBaseSaleAttrId());
                value.setSaleAttrName(saleAttr.getSaleAttrName());
                spuSaleAttrValueMapper.insert(value);
            });
        }
    }

    /**
     *  获取spu的图片
     * @param spuId
     * @return
     */
    @Override
    public List<SpuImage> spuImageListBySpuId(Long spuId) {
        SpuInfo spuInfo = spuInfoMapper.selectById(spuId);
        if (spuInfo != null) {
            LambdaQueryWrapper<SpuImage> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SpuImage::getSpuId, spuId);
            return spuImagesValueMapper.selectList(wrapper);
        }
        return Collections.emptyList();
    }

    /**
     *  获取到spu的销售属性集合
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> spuSaleAttrListBySpuId(Long spuId) {
        SpuInfo spuInfo = spuInfoMapper.selectById(spuId);
        if (spuInfo != null) {
            LambdaQueryWrapper<SpuSaleAttr> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SpuSaleAttr::getSpuId, spuId);
            List<SpuSaleAttr> attrs = spuSaleAttrMapper.selectList(wrapper);
            attrs.forEach(item -> {
                LambdaQueryWrapper<SpuSaleAttrValue> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(SpuSaleAttrValue::getBaseSaleAttrId, item.getBaseSaleAttrId());
                queryWrapper.eq(SpuSaleAttrValue::getSpuId, item.getSpuId());
                List<SpuSaleAttrValue> spuSaleAttrValues = spuSaleAttrValueMapper.selectList(queryWrapper);
                item.setSpuSaleAttrValueList(spuSaleAttrValues);
            });
            return attrs;
        }
        return Collections.emptyList();
    }

    /**
     * 获取指定spu下的一组销售属性以及对应的销售属性值集合
     * 根据指定的skuId为默认选中
     * 按照spu的基本销售属性排序
     * @param skuId
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        return spuSaleAttrMapper.getSpuSaleAttrListCheckBySku(skuId, spuId);
    }

    @Override
    public List<SpuPoster> findSpuPosterBySpuId(Long spuId) {
        SpuInfo info = spuInfoMapper.selectById(spuId);
        if (info != null) {
            LambdaQueryWrapper<SpuPoster> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SpuPoster::getSpuId, spuId);
            return spuPosterMapper.selectList(wrapper);
        }
        return Collections.emptyList();
    }

    /**
     *  根据SkuId获取平台属性和对应的平台属性值
     */
    @Override
    public List<BaseAttrInfo> getAttrListBySku(Long skuId) {
        List<BaseAttrInfo> attrInfos = baseAttrInfoMapper.getAttrListBySkuId(skuId);
        return attrInfos;
    }


    /**
     * 根据spuId 查询当前spu下所有的spu销售属性值组合id与skuId对应的map
     * key 的组装按照spu的基本销售属性值id排序 (根据 -> getSpuSaleAttrListCheckBySku)
     * 3732|3734: 21
     * 3732|3735: 25
     * 3733|3734: 26
     * 3733|3735: 27
     */
    @Override
    public Map getSkuValueIdsMap(Long spuId) {
        List<Map> skuValueIdsMapList = skuSaleAttrValueMapper.getSkuValueIdsMap(spuId);
        HashMap map = new HashMap(skuValueIdsMapList.size());
        skuValueIdsMapList.forEach(m->{
            map.put(m.get("value_ids"),m.get("sku_id"));
        });
        return map;

    }
}
