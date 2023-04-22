package com.wjl.gmall.product.service.impl;

import com.wjl.gamll.feign.client.FileServiceClient;
import com.wjl.gamll.feign.dto.FileInfo;
import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.product.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private FileServiceClient fileServiceClient;

    @Override
    public String uploadFile(MultipartFile file) {
        Result<FileInfo> result = fileServiceClient.uploadFormDate(file,null);
        String url = result.getData().getCurrentUrl();
        return url;
    }
}
