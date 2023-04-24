package com.wjl.gmall.weball.web;

import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.product.client.ProductServiceClient;
import com.wjl.gmall.product.client.model.vo.CategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/24
 * @description
 */
@Controller
public class IndexController {


    @Autowired
    private ProductServiceClient productServiceClient;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     *  首页数据特点
     *      1. 超高并发
     *        * 数据缓存
     *        * 显示方式  -> 生成静态页面 nginx / 模板渲染
     * @param model
     * @return
     */
    @GetMapping(value = {"index","index.html","/"})
    public String index(Model model){
        List<CategoryVO> categoryVOList = productServiceClient.getBaseCategoryList().getData();
        // 所有分类信息
        model.addAttribute("list",categoryVOList);
        return "index/index";
    }

    @ResponseBody
    @GetMapping("createIndex")
    public Result createIndex() throws IOException {
        List<CategoryVO> categoryVOList = productServiceClient.getBaseCategoryList().getData();
        Context context = new Context();
        context.setVariable("list",categoryVOList);
        templateEngine.process("index/index.html", context,new FileWriter("D:\\tmp\\gulimall-new\\day09es\\day09\\index.html"));
        return Result.ok();
    }
}
