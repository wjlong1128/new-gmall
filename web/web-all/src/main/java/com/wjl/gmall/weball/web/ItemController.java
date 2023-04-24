package com.wjl.gmall.weball.web;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.item.client.ItemServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/24
 * @description
 */
@Controller
public class ItemController {

    @Autowired
    private ItemServiceClient itemServiceClient;

    /**
     *  商品详情页
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("{skuId}.html")
    public String getItem(@PathVariable("skuId")Long skuId, Model model){
        Result<Map<String, Object>> detail = itemServiceClient.getSpuItemDetail(skuId);
        model.addAllAttributes(detail.getData());
        return "item/item";
    }

}
