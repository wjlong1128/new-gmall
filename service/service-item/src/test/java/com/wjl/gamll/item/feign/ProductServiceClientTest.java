package com.wjl.gamll.item.feign;

import com.wjl.gamll.item.ItemApplication;
import com.wjl.gamll.product.client.ProductServiceClient;
import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.model.product.SpuPoster;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/23
 * @description
 */
@SpringBootTest(classes = ItemApplication.class)
public class ProductServiceClientTest {

    @Autowired
    private ProductServiceClient productServiceClient;
    @Test
    void findSpuPosterBySpuId() {
        Result<List<SpuPoster>> result = productServiceClient.findSpuPosterBySpuId(10L);
        System.out.println(result.getData());
    }
}
