package com.wjl.gamll.feign.client;

import com.wjl.gamll.feign.config.MultipartFileConfig;
import com.wjl.gamll.feign.dto.FileInfo;
import com.wjl.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
@FeignClient(name = "file-server", configuration = MultipartFileConfig.class)
public interface FileServiceClient {
    @PostMapping(value = "/file/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<FileInfo> uploadFormDate(@RequestPart("file") MultipartFile file, @RequestParam(required = false,value = "objectName") String objectName);
}
