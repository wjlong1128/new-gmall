package com.wjl.gmall.product.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.model.product.BaseTrademark;
import com.wjl.gmall.product.service.BaseTrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
@RequestMapping("admin/product/baseTrademark")
@RestController
public class BaseTrademarkController {

    @Autowired
    private BaseTrademarkService baseTrademarkService;


    @GetMapping("{page}/{limit}")
    public Result<IPage<BaseTrademark>> getBaseTrademarkList(
            @PathVariable Long page,
            @PathVariable Long limit
    ) {
        IPage<BaseTrademark> data = baseTrademarkService.getBaseTrademarkList(page,limit);
        return Result.ok(data);
    }

    /**
     *  保存或更新商标品牌
     * @param baseTrademark
     * @return
     */
    @RequestMapping( value={"save","update"} ,method = {RequestMethod.POST,RequestMethod.PUT})
    public Result save(@RequestBody BaseTrademark baseTrademark ){
        if (baseTrademark.getId() != null) {
            baseTrademarkService.updateById(baseTrademark);
        }else {
            baseTrademarkService.save(baseTrademark);
        }
        return Result.ok();
    }
    @GetMapping("/get/{id}")
    public Result<BaseTrademark> getById(@PathVariable Long id){
        BaseTrademark byId = baseTrademarkService.getById(id);
        return Result.ok(byId);
    }

    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id){
        baseTrademarkService.removeById(id);
        return Result.ok();
    }
}
