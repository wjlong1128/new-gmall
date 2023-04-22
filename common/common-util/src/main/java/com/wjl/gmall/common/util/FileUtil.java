package com.wjl.gmall.common.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
public class FileUtil {
    public static String getDateTimePath(){
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd/"));
    }

    public static String fileMd5(File file) throws IOException {
        try (FileInputStream f = new FileInputStream(file)){
            String md5Hex = DigestUtils.md5Hex(f);
            return md5Hex;
        }
    }

    public static String getSuffix(String name){
        if (name == null || "".equals(name)){
            return "";
        }
        int i = name.lastIndexOf(".");
        if (i == -1){
            return "";
        }

        return name.substring(i);
    }


}
