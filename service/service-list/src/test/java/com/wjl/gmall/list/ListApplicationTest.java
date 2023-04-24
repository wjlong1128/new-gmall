package com.wjl.gmall.list;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/25
 * @description
 */
@SpringBootTest(classes = ListApplication.class)
public class ListApplicationTest {

    @Autowired
    private RestHighLevelClient client;
    @Test
    void testClient() throws IOException {
        boolean my = client.indices().exists(new GetIndexRequest("my"),RequestOptions.DEFAULT);
        System.out.println(my);
    }
}
