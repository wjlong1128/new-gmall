package com.wjl.gmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.gmall.model.product.BaseCategoryTrademark;
import com.wjl.gmall.model.product.BaseTrademark;
import com.wjl.gmall.model.product.CategoryTrademarkVo;
import com.wjl.gmall.product.mapper.BaseCategoryTrademarkMapper;
import com.wjl.gmall.product.service.BaseCategoryTrademarkService;
import com.wjl.gmall.product.service.BaseTrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
@Service
public class BaseCategoryTrademarkServiceImpl extends ServiceImpl<BaseCategoryTrademarkMapper, BaseCategoryTrademark> implements BaseCategoryTrademarkService {

    @Autowired
    private BaseTrademarkService trademarkService;

    @Override
    public List<BaseTrademark> findCurrentTrademarkList(Long category3Id) {
        List<Long> ids = this.baseMapper.selectTrademarkIdsByCategory3Id(category3Id);
        LambdaQueryWrapper<BaseTrademark> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.notIn(BaseTrademark::getId,ids);
        List<BaseTrademark> list = trademarkService.list(queryWrapper);
        return list;
    }

    @Override
    public void save(CategoryTrademarkVo categoryTrademarkVo) {
        List<Long> trademarkIdList = categoryTrademarkVo.getTrademarkIdList();
        List<BaseCategoryTrademark> list = trademarkIdList.stream().map(trademarkId -> {
            BaseCategoryTrademark categoryTrademark = new BaseCategoryTrademark();
            categoryTrademark.setTrademarkId(trademarkId);
            categoryTrademark.setCategory3Id(categoryTrademarkVo.getCategory3Id());
            return categoryTrademark;
        }).collect(Collectors.toList());

        saveBatch(list);
    }
}
