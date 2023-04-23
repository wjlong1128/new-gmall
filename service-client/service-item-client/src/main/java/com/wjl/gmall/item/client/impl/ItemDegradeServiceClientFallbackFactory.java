package com.wjl.gmall.item.client.impl;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.item.client.ItemServiceClient;
import feign.hystrix.FallbackFactory;

import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
public class ItemDegradeServiceClientFallbackFactory implements FallbackFactory<ItemServiceClient> {


    @Override
    public ItemServiceClient create(Throwable cause) {
        return new ItemServiceClient() {
            @Override
            public Result<Map<String,Object>> getSpuItemDetail(Long skuId) {
                return null;
            }
        };
    }
}
