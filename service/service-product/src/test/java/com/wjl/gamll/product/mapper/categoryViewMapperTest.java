package com.wjl.gamll.product.mapper;

import com.alibaba.fastjson.JSON;
import com.wjl.gmall.product.ProductApplication;
import com.wjl.gmall.product.mapper.CategoryViewMapper;
import com.wjl.gmall.product.model.entity.BaseCategoryView;
import com.wjl.gmall.product.model.vo.CategoryVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/24
 * @description
 */
@SpringBootTest(classes = ProductApplication.class)
public class categoryViewMapperTest {
    @Autowired
    CategoryViewMapper categoryViewMapper;


    @Test
    void testCategoryList() {
        ArrayList<CategoryVO> list = new ArrayList<>();

        List<BaseCategoryView> views = categoryViewMapper.selectList(null);
        // 视图查出所有，根据一级分类的id分组
        Map<Long, List<BaseCategoryView>> category1Map =
                views.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));


        int index = 1;
        for (Map.Entry<Long, List<BaseCategoryView>> entry : category1Map.entrySet()) {
            // key 一级分类id, value 一条数据
            //System.out.println("key:" + entry.getKey() + "   value:"+entry.getValue());
            CategoryVO categoryVO = new CategoryVO();
            String c1Name = entry.getValue().get(0).getCategory1Name();
            categoryVO.setCategoryName(c1Name);
            // index++
            categoryVO.setIndex(index++);
            categoryVO.setCategoryId(entry.getKey());
            ArrayList<CategoryVO.CategoryChild> category2Child = new ArrayList<>();
            // 获取二级分类
            Map<Long, List<BaseCategoryView>> category2Map = entry.getValue().stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            for (Map.Entry<Long, List<BaseCategoryView>> c2Entry : category2Map.entrySet()) {
                CategoryVO.CategoryChild e = new CategoryVO.CategoryChild();
                e.setCategoryId(c2Entry.getKey());
                e.setCategoryName(c2Entry.getValue().get(0).getCategory2Name());

                ArrayList<CategoryVO.CategoryChild> c2Child = new ArrayList<>();
                Set<Map.Entry<Long, List<BaseCategoryView>>> category3Map = c2Entry.getValue().stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory3Id)).entrySet();
                for (Map.Entry<Long, List<BaseCategoryView>> c3Entry : category3Map) {
                    c2Child.add(new CategoryVO.CategoryChild(c3Entry.getKey(),c3Entry.getValue().get(0).getCategory3Name(),null));
                }

                e.setCategoryChild(c2Child);

                category2Child.add(e);
            }
            categoryVO.setCategoryChild(category2Child);
            list.add(categoryVO);
        }


        System.out.println(JSON.toJSONString(list));
    }
}
