package com.wjl.gmall.model.dto;

import lombok.Data;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
@Data
public class FileInfo {
    private String originalName;
    private String bucket;
    private String path;
    private String url;
    private Long size;
    private String contentType;
    private String fileMd5;

    private String currentUrl;
}
