package com.wjl.gmall.product.controller;

import com.wjl.gmall.model.product.*;
import com.wjl.gmall.product.service.BaseManagerService;
import com.wjl.gmall.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/21
 * @description
 */
@RequestMapping("admin/product")
@RestController
@RequiredArgsConstructor
public class BaseManagerController {

    private final BaseManagerService baseManagerService;

    @GetMapping("/getCategory1")
    public Result<List<BaseCategory1>> getCategory1(){
        List<BaseCategory1> category1s = baseManagerService.getCategory1();
        return Result.ok(category1s);
    }

    @GetMapping("/getCategory2/{category1Id}")
    public Result<List<BaseCategory2>> getCategory2List(@PathVariable("category1Id") Long id){
        List<BaseCategory2> category2s = baseManagerService.getCategory2List(id);
        return Result.ok(category2s);
    }


    @GetMapping("/getCategory3/{category2Id}")
    public Result<List<BaseCategory3>> getCategory3List(@PathVariable("category2Id") Long id){
        List<BaseCategory3> category3s = baseManagerService.getCategory3List(id);
        return Result.ok(category3s);
    }


    @GetMapping("/attrInfoList/{category1}/{category2}/{category3}")
    public Result<List<BaseAttrInfo>> getAttrInfoList(@PathVariable("category1") Long category1Id, @PathVariable("category2") Long category2Id, @PathVariable("category3") Long category3Id){
        List<BaseAttrInfo> baseAttrInfos = baseManagerService.getAttrInfoList(category1Id,category2Id,category3Id);
        return Result.ok(baseAttrInfos);
    }

    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        baseManagerService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    @GetMapping("/getAttrValueList/{attrId}")
    public Result<List<BaseAttrValue>> getAttrValueList(@PathVariable("attrId") Long attrId){
        List<BaseAttrValue> attrValueList = baseManagerService.getAttrValueList(attrId);
        return Result.ok(attrValueList);
    }

}
