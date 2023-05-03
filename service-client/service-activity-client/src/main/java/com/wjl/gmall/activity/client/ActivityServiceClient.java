package com.wjl.gmall.activity.client;

import com.wjl.gmall.activity.client.impl.ActivityDegradeServiceFallbackFactory;
import com.wjl.gmall.activity.dto.SeckillGoods;
import com.wjl.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/3
 * @description
 */
@FeignClient(value = "service-activity", fallbackFactory = ActivityDegradeServiceFallbackFactory.class)
public interface ActivityServiceClient {

    @GetMapping("api/activity/seckill/findAll")
    public Result<List<SeckillGoods>> findAll();

    @GetMapping("api/activity/seckill/find/{skuId}")
    public Result<SeckillGoods> findBySkuId(@PathVariable("skuId") Long skuId);
}
