package com.wjl.gmall.weball.web;

import com.wjl.gmall.list.client.ListServiceClient;
import com.wjl.gmall.list.model.query.SearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/25
 * @description
 */
@Controller
public class SearchController {

    @Autowired
    private ListServiceClient listServiceClient;

    @GetMapping("list.html")
    public String list(SearchParam param, Model model) {
        Map<String, Object> data = listServiceClient.search(param).getData();
        model.addAllAttributes(data);
        String urlParam = this.makeUrlParam(param);
        model.addAttribute("urlParam", urlParam);
        model.addAttribute("searchParam", param);
        return "list/index";
    }

    /**
     * 拼接url路径
     *
     * @param searchParam
     * @return
     */
    private String makeUrlParam(SearchParam searchParam) {
        StringBuilder urlParam = new StringBuilder();
        // 判断关键字
        if (searchParam.getKeyword() != null) {
            urlParam.append("keyword=").append(searchParam.getKeyword());
        }
        // 判断一级分类
        if (searchParam.getCategory1Id() != null) {
            urlParam.append("category1Id=").append(searchParam.getCategory1Id());
        }
        // 判断二级分类
        if (searchParam.getCategory2Id() != null) {
            urlParam.append("category2Id=").append(searchParam.getCategory2Id());
        }
        // 判断三级分类
        if (searchParam.getCategory3Id() != null) {
            urlParam.append("category3Id=").append(searchParam.getCategory3Id());
        }
        // 以上四个都是点击首页或者搜索关键字跳转页面的，所以直接是一级，不用加&
        // 处理品牌
        if (searchParam.getTrademark() != null) {
            if (urlParam.length() > 0) {
                urlParam.append("&trademark=").append(searchParam.getTrademark());
            }
        }
        // 判断平台属性值
        if (null != searchParam.getProps()) {
            for (String prop : searchParam.getProps()) {
                if (urlParam.length() > 0) {
                    urlParam.append("&props=").append(prop);
                }
            }
        }
        return "list.html?" + urlParam.toString();
    }
}
