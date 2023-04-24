package com.wjl.gmall.product.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/24
 * @description
 *
 */
@Data
public class CategoryVO {
    private int index;
    private Long categoryId;
    private String categoryName;
    private List<CategoryChild> categoryChild;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class  CategoryChild{
        private Long categoryId;
        private String categoryName;
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
        List<CategoryChild> categoryChild;
    }

    /**
     * [
     *     {
     *         "index": 1,
     *         "categoryChild": [
     *             {
     *                 "categoryChild": [
     *                     {
     *                         "categoryName": "电子书",
     *                         "categoryId": 1
     *                     },
     *                     {
     *                         "categoryName": "网络原创",
     *                         "categoryId": 2
     *                     }
     *                 ],
     *                 "categoryName": "电子书刊",
     *                 "categoryId": 1
     *             }
     *         ],
     *         "categoryName": "图书、音像、电子书刊",
     *         "categoryId": 1
     *     },
     *     {
     *         "index": 2,
     *         "categoryChild": [
     *             {
     *                 "categoryChild": [
     *                     {
     *                         "categoryName": "超薄电视",
     *                         "categoryId": 1
     *                     },
     *                     {
     *                         "categoryName": "全面屏电视",
     *                         "categoryId": 2
     *                     }
     *                 ],
     *                 "categoryName": "电视",
     *                 "categoryId": 1
     *             }
     *         ],
     *         "categoryName": "家用电器",
     *         "categoryId": 2
     *     }
     * ]
     */
}
