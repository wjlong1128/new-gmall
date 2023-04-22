package com.wjl.gamll.file.config;

import com.wjl.gamll.file.config.properties.MinioProperties;
import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient(MinioProperties properties) {
        String accessKey = properties.getAccessKey();
        String secreKey = properties.getSecreKey();
        String endpointUrl = properties.getEndpointUrl();
        return MinioClient
                .builder()
                .endpoint(endpointUrl)
                .credentials(accessKey, secreKey)
                .build();
    }

}
