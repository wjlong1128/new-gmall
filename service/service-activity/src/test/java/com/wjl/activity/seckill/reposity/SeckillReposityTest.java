package com.wjl.activity.seckill.reposity;

import com.wjl.gmall.acitvity.ActivityApplication;
import com.wjl.gmall.acitvity.model.entity.SeckillGoods;
import com.wjl.gmall.acitvity.repository.SeckillRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/3
 * @description
 */
@SpringBootTest(classes = ActivityApplication.class)
public class SeckillReposityTest {

    @Autowired
    SeckillRepository seckillRepository;

    @Test
    void getSeckillsWithLastDay() {
        List<SeckillGoods> seckillsWithLastDay = seckillRepository.getSeckillsWithLastDay(0);
        System.out.println(seckillsWithLastDay);
    }


    @Test
    void getOverDueSeckill() {
        List<Long> overDueSeckill = this.seckillRepository.getOverDueSeckillSkuIds();
        System.out.println(overDueSeckill);
    }
}
