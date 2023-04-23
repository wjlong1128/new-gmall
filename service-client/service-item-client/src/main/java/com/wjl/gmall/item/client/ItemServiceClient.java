package com.wjl.gmall.item.client;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.item.client.impl.ItemDegradeServiceClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
@FeignClient(value = "service-item",fallbackFactory = ItemDegradeServiceClientFallbackFactory.class)
public interface ItemServiceClient {
    @GetMapping("/api/item/{skuId}")
    public Result<Map<String,Object>> getSpuItemDetail(@PathVariable("skuId") Long skuId);
}
