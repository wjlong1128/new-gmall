package com.wjl.gamll.product.mapper;

import com.wjl.gmall.product.ProductApplication;
import com.wjl.gmall.product.mapper.SpuSaleAttrMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
@SpringBootTest(classes = ProductApplication.class)
public class SpuSaleAttrValueTest {

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Test
    void getSpuSaleAttrListCheckBySku() {
        System.out.println(spuSaleAttrMapper.getSpuSaleAttrListCheckBySku(21l, 10l));
    }
}
