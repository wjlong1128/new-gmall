package com.wjl.gmall.product.controller.admin;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.product.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/24
 * @description
 */
@RequestMapping("/admin/product")
@RestController
public class TestController {

@Autowired
private TestService testService;

    @GetMapping("testLock")
    public Result<String> testLock(){
        String num = testService.testLock();
        return Result.ok(num);
    }

}
