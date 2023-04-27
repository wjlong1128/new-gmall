package com.wjl.gmall.weball.web;

import com.wjl.gmall.product.client.ProductServiceClient;
import com.wjl.gmall.product.client.model.dto.SkuInfo;
import com.wjl.gmall.weball.model.vo.AddToCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/27
 * @description
 */
@Controller
public class CartController {


    @Autowired
    private ProductServiceClient productServiceClient;

    @GetMapping("/addCart.html")
    public String addCart(AddToCart addToCart, Model model) {
        SkuInfo skuInfo = productServiceClient.getSkuInfoAndImages(addToCart.getSkuId()).getData();
        model.addAttribute("skuInfo", skuInfo);
        model.addAttribute("skuNum",addToCart.getSkuNum());
        return "cart/addCart";
    }

    /**
     * 跳转到展示页面
     * @return
     */
    @GetMapping("/cart.html")
    public String cartIndex() {
        return "cart/index";
    }

}
