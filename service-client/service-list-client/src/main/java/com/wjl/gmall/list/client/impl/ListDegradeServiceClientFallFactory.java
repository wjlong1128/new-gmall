package com.wjl.gmall.list.client.impl;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.list.client.ListServiceClient;
import com.wjl.gmall.list.model.query.SearchParam;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/25
 * @description
 */
@Component
public class ListDegradeServiceClientFallFactory implements FallbackFactory<ListServiceClient> {
    @Override
    public ListServiceClient create(Throwable cause) {
        return new ListServiceClient() {
            @Override
            public Result upperGoods(Long skuId) {
                return Result.fail();
            }

            @Override
            public Result lowerGoods(Long skuId) {
                return Result.fail();
            }

            @Override
            public Result incrHotScore(Long skuId) {
                return Result.fail();
            }

            @Override
            public Result createIndex() {
                return Result.fail();
            }

            @Override
            public Result<Map<String,Object>> search(SearchParam param) {
                return Result.fail();
            }

        };
    }
}
