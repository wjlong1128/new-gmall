package com.wjl.gmall.activity.client.impl;

import com.wjl.gmall.activity.client.ActivityServiceClient;
import com.wjl.gmall.activity.dto.SeckillGoods;
import com.wjl.gmall.common.result.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/3
 * @description
 */
@Component
public class ActivityDegradeServiceFallbackFactory implements FallbackFactory<ActivityServiceClient> {
    @Override
    public ActivityServiceClient create(Throwable cause) {
        return new ActivityServiceClient() {
            @Override
            public Result<List<SeckillGoods>> findAll() {
                return Result.fail();
            }

            @Override
            public Result<SeckillGoods> findBySkuId(Long skuId) {
                return Result.fail();
            }
        };
    }
}
