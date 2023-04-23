package com.wjl.gmall.product.controller.admin;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.model.product.BaseTrademark;
import com.wjl.gmall.model.product.CategoryTrademarkVo;
import com.wjl.gmall.product.service.BaseCategoryTrademarkService;
import com.wjl.gmall.product.service.BaseTrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
@RequestMapping("admin/product/baseCategoryTrademark")
@RestController
public class BaseCategoryTrademarkController {

    @Autowired
    private BaseTrademarkService baseTrademarkService;

    @Autowired
    private BaseCategoryTrademarkService baseCategoryTrademarkService;

    /**
     *  根据三级分类获取商家集合
     * @param id
     * @return
     */
    @GetMapping("/findTrademarkList/{id}")
    public Result<List<BaseTrademark>> findTrademarkList(@PathVariable Long id) {
        List<BaseTrademark> list = baseTrademarkService.findTrademarkListByCategory3Id(id);
        return Result.ok(list);
    }


    /**
     *  删除三级分类下的商家信息
     * @param category3Id
     * @param trademarkId
     * @return
     */
    @DeleteMapping("remove/{category3Id}/{trademarkId}")
    public Result removeCategoryTrademark(
            @PathVariable("category3Id") Long category3Id,
            @PathVariable("trademarkId") Long trademarkId
    ) {
        baseTrademarkService.removeCategoryTrademark(category3Id,trademarkId);
        return Result.ok();
    }


    /**
     * 根据当前三级分类获取商家集合
     * @param category3Id
     * @return
     */
    @GetMapping("/findCurrentTrademarkList/{category3Id}")
    public Result<List<BaseTrademark>> findCurrentTrademarkList(@PathVariable Long category3Id){
        List<BaseTrademark> list  =  baseCategoryTrademarkService.findCurrentTrademarkList(category3Id);
        return Result.ok(list);
    }


    /**
     *  关联商家和三级分类
     * @param categoryTrademarkVo
     * @return
     */
    @PostMapping("save")
    public Result save(@RequestBody CategoryTrademarkVo categoryTrademarkVo){
        baseCategoryTrademarkService.save(categoryTrademarkVo);
        return Result.ok();
    }

}
