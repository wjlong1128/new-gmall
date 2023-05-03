package com.wjl.gmall.weball.web;

import com.wjl.gmall.activity.client.ActivityServiceClient;
import com.wjl.gmall.activity.dto.SeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/4
 * @description
 */
@Controller
public class SeckillController {

    @Autowired
    private ActivityServiceClient activityServiceClient;

    /**
     * 秒杀商品列表
     *
     * @param model
     * @return
     */
    @GetMapping("seckill.html")
    public String seckill(Model model) {
        List<SeckillGoods> data = activityServiceClient.findAll().getData();
        model.addAttribute("list", data);
        return "seckill/index";
    }

    /**
     * 秒杀商品详情
     *
     * @param model
     * @param skuId
     * @return
     */
    @GetMapping("seckill/{skuId}.html")
    public String seckillDetail(Model model, @PathVariable("skuId") Long skuId) {
        SeckillGoods data = activityServiceClient.findBySkuId(skuId).getData();
        model.addAttribute("item", data);
        return "seckill/item";
    }


    /**
     * 抢单页面
     *
     * @return
     */
    @GetMapping("seckill/queue.html")
    public String seckillQueue(HttpServletRequest request) {
        String skuId = request.getParameter("skuId");
        String skuIdStr = request.getParameter("skuIdStr");
        request.setAttribute("skuId", skuId);
        request.setAttribute("skuIdStr", skuIdStr);
        return "seckill/queue";
    }
}
