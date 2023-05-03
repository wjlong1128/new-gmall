package com.wjl.gmall.acitvity.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wjl.gmall.acitvity.mapper.SeckillMapper;
import com.wjl.gmall.acitvity.model.entity.SeckillGoods;
import com.wjl.gmall.acitvity.repository.SeckillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/3
 * @description
 */
@Repository
public class DefaultSeckillRepository implements SeckillRepository {

    @Autowired
    private SeckillMapper seckillMapper;

    @Override
    public List<SeckillGoods> getSeckillsWithLastDay(int day) {
        LambdaQueryWrapper<SeckillGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SeckillGoods::getStatus, 1);
        queryWrapper.gt(SeckillGoods::getStockCount, 0);
        LocalDateTime currentMinTime = this.getCurrentMinTime();
        LocalDateTime days = this.getDaysTime(day);
        // 大于等于开始时间
        queryWrapper.ge(SeckillGoods::getStartTime, currentMinTime);
        // 小于等于结束时间
        queryWrapper.le(SeckillGoods::getEndTime, days);
        return seckillMapper.selectList(queryWrapper);
    }

    @Override
    public void updateSeckillGoodsStock(Long skuId,  Integer stockSize) {
        LambdaQueryWrapper<SeckillGoods> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(SeckillGoods::getSkuId,skuId);
        SeckillGoods entity = new SeckillGoods();
        entity.setStockCount(stockSize.intValue());
        entity.setSkuId(skuId);
        int update = this.seckillMapper.update(entity, updateWrapper);
        if (update <= 0){
            throw new RuntimeException("更新库存失败");
        }
    }

    /**
     * @return
     */
    private LocalDateTime getCurrentMinTime() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
    }

    /**
     * 获取day天之后的最大时间
     *
     * @param day
     * @return
     */
    private LocalDateTime getDaysTime(int day) {
        return LocalDateTime.of(LocalDate.now().plusDays(day), LocalTime.MAX);
    }


}
