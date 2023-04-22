package com.wjl.gamll.file.api;

import com.wjl.gamll.file.model.dto.FileInfo;
import com.wjl.gamll.file.service.UploadFileService;
import com.wjl.gmall.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
@RequestMapping("upload")
@RestController
public class UploadFileController {

    @Autowired
    private UploadFileService fileService;

    @PostMapping
    public Result<FileInfo> uploadFormDate(@RequestPart("file") MultipartFile file, @RequestParam(value = "objectName",required = false) String objectName) {
        FileInfo fileInfo = fileService.uploadWithStreamNoMd5(file,objectName);
        return Result.ok(fileInfo);
    }

}
