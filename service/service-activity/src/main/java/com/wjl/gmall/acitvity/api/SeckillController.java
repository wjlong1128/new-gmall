package com.wjl.gmall.acitvity.api;

import com.wjl.gmall.acitvity.model.entity.SeckillGoods;
import com.wjl.gmall.acitvity.service.SeckillService;
import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.common.util.AuthContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/4
 * @description
 */
@RequestMapping("api/activity/seckill")
@RestController
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * 查询redis中秒杀商品
     *
     * @return
     */
    @GetMapping("/findAll")
    public Result<List<SeckillGoods>> findAll() {
        List<SeckillGoods> goods = this.seckillService.findAll();
        return Result.ok(goods);
    }


    /**
     * 查询秒杀 商品
     *
     * @param skuId
     * @return
     */
    @GetMapping("/find/{skuId}")
    public Result<SeckillGoods> findBySkuId(@PathVariable("skuId") Long skuId) {
        SeckillGoods goods = this.seckillService.findBySkuId(skuId);
        return Result.ok(goods);
    }


    /**
     * 获取下单随机码
     *
     * @param skuId
     * @return
     */
    @GetMapping("/auth/getSeckillSkuIdStr/{skuId}")
    public Result<String> getSeckillSkuIdStr(@PathVariable("skuId") Long skuId, HttpServletRequest request) {
        String userId = AuthContextHolder.getUserId(request);
        String randomCode = this.seckillService.getRandomCode(skuId, userId);
        return Result.ok(randomCode);
    }


    // seckillOrder/28?skuIdStr=c4ca4238a0b923820dcc509a6f75849b

    /**
     * 点击立即抢购，跳转排队页面，带着随机码进行下单
     *
     * @param request
     * @param skuId
     * @param randomCode
     * @return
     */
    @PostMapping("/auth/seckillOrder/{skuId}")
    public Result seckillOrder(HttpServletRequest request, @PathVariable("skuId") Long skuId, @RequestParam("skuIdStr") String randomCode) {
        String userId = AuthContextHolder.getUserId(request);
        this.seckillService.seckillOrder(skuId,userId,randomCode);
        return Result.ok();
    }

}
