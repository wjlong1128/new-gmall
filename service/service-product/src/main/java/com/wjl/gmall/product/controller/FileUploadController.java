package com.wjl.gmall.product.controller;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.product.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
@RequestMapping("admin/product")
@RestController
public class FileUploadController {

    @Autowired
    private FileUploadService uploadService;

    @PostMapping("fileUpload")

    public Result<String> fileUpload(@RequestPart("file") MultipartFile file){
        String url =uploadService.uploadFile(file);
        return Result.ok(url);
    }

}
