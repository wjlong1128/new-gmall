package com.wjl.gmall.product.service;

import org.springframework.web.multipart.MultipartFile;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
public interface FileUploadService {
    String uploadFile(MultipartFile file);
}
