package com.wjl.gmall.cart.api;

import com.wjl.gmall.cart.model.entity.CartInfo;
import com.wjl.gmall.cart.service.CartService;
import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.common.util.AuthContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/27
 * @description
 */
@RequestMapping("api/cart")
@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加购物车
     *
     * @param skuId
     * @param skuNum
     * @return
     */
    @GetMapping("addToCart/{skuId}/{skuNum}")
    public Result addToCart(
            @PathVariable("skuId") Long skuId,
            @PathVariable("skuNum") Integer skuNum,
            HttpServletRequest request
    ) {

        String userId = getUserId(request);
        cartService.addToCart(skuId, skuNum, userId);
        return Result.ok();
    }

    /**
     * 展示购物车
     *
     * @return
     */
    @GetMapping("cartList")
    public Result<List<CartInfo>> cartList(HttpServletRequest request) {
        String userId = AuthContextHolder.getUserId(request);
        String userTempId = AuthContextHolder.getUserTempId(request);
        List<CartInfo> cartInfoList = cartService.cartList(userId, userTempId);
        return Result.ok(cartInfoList);
    }


    /**
     * 选中
     *
     * @param skuId
     * @param isChecked
     * @param request
     * @return
     */

    @GetMapping("checkCart/{skuId}/{isChecked}")
    public Result checkCart(
            @PathVariable("skuId") Long skuId,
            @PathVariable("isChecked") Integer isChecked,
            HttpServletRequest request
    ) {
        String userId = getUserId(request);
        cartService.check(skuId, isChecked, userId);
        return Result.ok();
    }

    private static String getUserId(HttpServletRequest request) {
        String userId = AuthContextHolder.getUserId(request);
        if (StringUtils.isEmpty(userId)) {
            userId = AuthContextHolder.getUserTempId(request);
        }
        return userId;
    }


    /**
     * 删除购物车商品
     *
     * @param skuId
     * @param request
     * @return
     */
    @DeleteMapping("deleteCart/{skuId}")
    public Result deleteCart(@PathVariable("skuId") Long skuId, HttpServletRequest request) {
        String userId = getUserId(request);
        cartService.deleteCartSku(skuId,userId);
        return Result.ok();
    }

    /**
     *  获取已经选中的购物车列表
     * @param userId
     * @return
     */
    @GetMapping("/getCartCheckedList/{userId}")
    public List<CartInfo> getCartCheckedList(@PathVariable("userId") Long userId){
        return cartService.getCartCheckedList(userId);
    }

}
