package com.wjl.gamll.product.feign;

import com.wjl.gamll.feign.client.FileServiceClient;
import com.wjl.gamll.feign.dto.FileInfo;
import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.common.utils.FileUtil;
import com.wjl.gmall.product.ProductApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/22
 * @description
 */
//@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProductApplication.class)
public class UploadFileServiceTest {

    @Autowired
    FileServiceClient fileServiceClient;

    @Test
    public void testUploadFormDate() throws IOException {
        MultipartFile file = FileUtil.getFormMultipartFile("file", new File("D:\\gmall\\gmall-parent\\a_file\\common.yaml"));
        Result<FileInfo> fileInfoResult = fileServiceClient.uploadFormDate(file,null);
        System.out.println(fileInfoResult.getData());
    }

}
