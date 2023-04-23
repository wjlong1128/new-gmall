package com.wjl.gamll.product.mapper;

import com.wjl.gmall.product.ProductApplication;
import com.wjl.gmall.product.mapper.SkuInfoMapper;
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
public class SkuInfoMapperTest {
@Autowired
private SkuInfoMapper skuInfoMapper;
    @Test
    void getPrice() {
        System.out.println(skuInfoMapper.getPrice(21L));
    }
}
