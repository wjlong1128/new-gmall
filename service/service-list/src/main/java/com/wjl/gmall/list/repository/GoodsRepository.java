package com.wjl.gmall.list.repository;

import com.wjl.gmall.list.model.document.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @date 2023/4/25
 * @description
 *  自动扫描
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
