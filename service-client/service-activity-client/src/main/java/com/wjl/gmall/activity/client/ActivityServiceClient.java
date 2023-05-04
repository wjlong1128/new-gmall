package com.wjl.gmall.activity.client;

import com.wjl.gmall.activity.client.impl.ActivityDegradeServiceFallbackFactory;
import com.wjl.gmall.activity.dto.SeckillGoods;
import com.wjl.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

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


    /**
     * 获取用户下单页
     *
     * @param skuId
     * @return
     */
    @GetMapping("api/activity/seckill/auth/trade")
    public Result<Map<String, Object>> trade(@RequestParam("skuId") String skuId);
}
