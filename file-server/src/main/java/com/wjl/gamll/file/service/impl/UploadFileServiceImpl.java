package com.wjl.gamll.file.service.impl;

import com.wjl.gamll.file.config.properties.MinioProperties;
import com.wjl.gamll.file.model.dto.FileInfo;
import com.wjl.gamll.file.service.UploadFileService;
import com.wjl.gmall.common.util.FileUtil;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
@Service
public class UploadFileServiceImpl implements UploadFileService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioProperties properties;

    @Override
    public FileInfo uploadWithTemp(MultipartFile file, String objectName) {
        try {
            String contentType = file.getContentType();
            String originalFilename = file.getOriginalFilename();
            String bucketName = properties.getBucketName();
            String pre = UUID.randomUUID().toString().substring(0, 5);
            File tempFile = File.createTempFile(pre, originalFilename);
            file.transferTo(tempFile);
            String fileMd5 = FileUtil.fileMd5(tempFile);
            if (objectName == null || "".equals(objectName)) {
                String newFileName = fileMd5 + FileUtil.getSuffix(originalFilename);
                objectName = FileUtil.getDateTimePath() + newFileName;
            }
            minioClient.uploadObject(UploadObjectArgs.builder().bucket(bucketName).object(objectName).filename(tempFile.getAbsolutePath()).build());
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileMd5(fileMd5);
            fileInfo.setBucket(bucketName);
            fileInfo.setContentType(contentType);
            fileInfo.setSize(tempFile.length());
            fileInfo.setPath(objectName);
            fileInfo.setOriginalName(originalFilename);
            fileInfo.setUrl("/" + bucketName + "/" + objectName);
            fileInfo.setCurrentUrl(properties.getEndpointUrl() + fileInfo.getUrl());
            return fileInfo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileInfo uploadWithStreamNoMd5(MultipartFile file, String objectName) {
        try {
            String bucketName = properties.getBucketName();
            String contentType = file.getContentType();
            String originalFilename = file.getOriginalFilename();

            if (objectName == null) {
                objectName = FileUtil.getDateTimePath() + UUID.randomUUID().toString() + FileUtil.getSuffix(originalFilename);
            }
            long size = file.getSize();
            PutObjectArgs objectArgs = PutObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .contentType(contentType)
                    .object(objectName)
                    .stream(file.getInputStream(), size, -1)
                    .build();
            minioClient.putObject(objectArgs);
            FileInfo fileInfo = new FileInfo();

            fileInfo.setBucket(bucketName);
            fileInfo.setContentType(contentType);
            fileInfo.setSize(size);
            fileInfo.setPath(objectName);
            fileInfo.setOriginalName(originalFilename);
            fileInfo.setUrl("/" + bucketName + "/" + objectName);
            fileInfo.setCurrentUrl(properties.getEndpointUrl() + fileInfo.getUrl());
            return fileInfo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
