package com.wjl.gamll.product.mapper;

import com.wjl.gmall.product.ProductApplication;
import com.wjl.gmall.product.mapper.SkuImageMapper;
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
public class SkuImageMapperTest {
    @Autowired
    private SkuImageMapper skuImageMapper;

    @Test
    void getSkuImages() {
        System.out.println(skuImageMapper.getSkuImages(21L));
    }


}
