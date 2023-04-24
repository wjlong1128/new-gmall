package com.wjl.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.gmall.product.model.entity.BaseCategoryTrademark;
import com.wjl.gmall.product.model.entity.BaseTrademark;
import com.wjl.gmall.product.mapper.BaseCategoryTrademarkMapper;
import com.wjl.gmall.product.mapper.BaseTrademarkMapper;
import com.wjl.gmall.product.service.BaseTrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
@Service
public class BaseTrademarkServiceImpl extends ServiceImpl<BaseTrademarkMapper, BaseTrademark> implements BaseTrademarkService {

    @Autowired
    private BaseCategoryTrademarkMapper baseCategoryTrademarkMapper;



    @Override
    public IPage<BaseTrademark> getBaseTrademarkList(Long page, Long limit) {
        return page(new Page<>(page,limit),null);
    }

    /**
     *  根据三级分类获取商家集合
     * @param id
     * @return
     */
    @Override
    public List<BaseTrademark> findTrademarkListByCategory3Id(Long id) {
        List<Long> ids =  baseCategoryTrademarkMapper.selectTrademarkIdsByCategory3Id(id);
        if (CollectionUtils.isEmpty(ids)){
            return Collections.emptyList();
        }
        List<BaseTrademark> list = this.baseMapper.selectTrademarkByIds(ids);
        return list;
    }

    /**
     * 删除三级分类和商家品牌的关联信息
     * @param category3Id
     * @param trademarkId
     */
    @Override
    public void removeCategoryTrademark(Long category3Id, Long trademarkId) {
        QueryWrapper<BaseCategoryTrademark> wrapper = new QueryWrapper<>();
        wrapper
                .eq("category3_id",category3Id)
                .eq("trademark_id",trademarkId);
        baseCategoryTrademarkMapper.delete(wrapper);
    }
}
