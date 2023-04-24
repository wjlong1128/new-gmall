package com.wjl.gmall.product.controller.admin;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.product.model.entity.BaseSaleAttr;
import com.wjl.gmall.product.service.BaseSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
@RequestMapping("admin/product")
@RestController
public class BaseSaleAttrController {


    @Autowired
    private BaseSaleAttrService baseSaleAttrService;


    /**
     *  获取平台销售属性集合
     * @return
     */
    @GetMapping("/baseSaleAttrList")
    public Result<List<BaseSaleAttr>> baseSaleAttrList() {
        List<BaseSaleAttr> baseSaleAttrs = baseSaleAttrService.list();
        return Result.ok(baseSaleAttrs);
    }




}
