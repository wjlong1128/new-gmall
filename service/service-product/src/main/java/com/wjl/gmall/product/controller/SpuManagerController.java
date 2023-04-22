package com.wjl.gmall.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.model.product.SpuInfo;
import com.wjl.gmall.product.service.BaseManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
@RequestMapping("admin/product")
@RestController
@RequiredArgsConstructor
public class SpuManagerController {

    private final BaseManagerService baseManagerService;

    @GetMapping("{page}/{limit}")
    public Result<Page<SpuInfo>> getSpuInfoList(
            @PathVariable Long page,
            @PathVariable Long limit,
            SpuInfo spuInfo
    ) {
        Page<SpuInfo> spuInfoPage = baseManagerService.getSpuInfoList(page, limit, spuInfo);
        return Result.ok(spuInfoPage);
    }

}
