package com.wjl.gmall.common.utils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
public class FileUtil {

    public static MultipartFile getFormMultipartFile(String fileField, File file) throws IOException {
        String fileName = file.getName();
        FileItem item = new DiskFileItemFactory().createItem(fileField, getContentType(file.getName()), true, fileName);
        try(FileInputStream in = new FileInputStream(file); OutputStream os = item.getOutputStream()){
            IOUtils.copy(in,os);
            return new CommonsMultipartFile(item);
        }
    }

    public static String getContentType(String fileName) {
        return MediaTypeFactory.getMediaType(fileName).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
    }

}
