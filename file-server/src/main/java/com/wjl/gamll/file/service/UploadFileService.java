package com.wjl.gamll.file.service;

import com.wjl.gamll.file.model.dto.FileInfo;
import org.springframework.web.multipart.MultipartFile;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
public interface UploadFileService {

    FileInfo uploadWithTemp(MultipartFile file,String objectName);
    FileInfo uploadWithStreamNoMd5(MultipartFile file, String objectName);
}
