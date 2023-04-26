package com.wjl.gmall.weball.web;

import com.wjl.gmall.list.client.ListServiceClient;
import com.wjl.gmall.list.model.query.SearchParam;
import com.wjl.gmall.weball.model.vo.OrderMap;
import com.wjl.gmall.weball.model.vo.UrlProp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


    // ?category3Id=61&trademark=2:华为&props=23:4G:运行内存&order=1:desc
    @GetMapping("list.html")
    public String list(SearchParam param, Model model) {
        Map<String, Object> data = listServiceClient.search(param).getData();
        model.addAllAttributes(data);

        // 拼装前端需要的模板数据
        String urlParam = this.makeUrlParam(param);
        String trademarkParam = this.makeTrademarkParams(param.getTrademark());
        List<UrlProp> urlProps = this.makeUrlProps(param.getProps());
        OrderMap orderMap = this.makeOrderMap(param.getOrder());
        model.addAttribute("urlParam", urlParam);
        model.addAttribute("searchParam", param);
        model.addAttribute("trademarkParam", trademarkParam);
        model.addAttribute("propsParamList", urlProps);

        model.addAttribute("orderMap",orderMap);
        return "list/index";
    }

    private OrderMap makeOrderMap(String order) {
        if (StringUtils.hasText(order) ){
            String[] split = order.split(":");
            if (split.length == 2){
                return new OrderMap(split[0],split[1]);
            }
        }
        // 1. hotScore 2. price
        return new OrderMap("1","desc");
    }

    /**
     * 面包屑导航平台属性
     *
     * @param props
     * @return
     */
    private List<UrlProp> makeUrlProps(String[] props) {
        if (props != null && props.length > 0) {
            return Arrays.asList(props)
                    .stream()
                    .map(prop -> prop.split(":"))
                    .filter(prop -> prop.length == 3)
                    .map(prop -> new UrlProp(prop[0], prop[2], prop[1]))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 面包屑导航，品牌
     *
     * @param trademark
     * @return
     */
    private String makeTrademarkParams(String trademark) {
        if (!StringUtils.hasText(trademark)) {
            // 如果为空返回字符串，防止前端模板为空报错
            return "";
        }
        String[] split = trademark.split(":");
        if (split.length == 2) {
            return "品牌" + split[1];
        }
        return "";
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
