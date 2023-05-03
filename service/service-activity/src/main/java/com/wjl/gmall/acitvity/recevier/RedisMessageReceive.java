package com.wjl.gmall.acitvity.recevier;

import com.wjl.gmall.acitvity.service.SeckillService;
import com.wjl.gmall.acitvity.util.CacheHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/3
 * @description
 */
@Component
public class RedisMessageReceive {

    @Autowired
    private SeckillService seckillService;

    /**
     * 订阅消息，将redis缓存中的数据添加至本地内存状态位
     *
     * @param message
     */
    public void receiveMessage(String message) {
        // 清除无用的双引号
        message = message.replace("\"", "");
        String[] split = message.split(":");
        if (split.length > 1) {
            String goodsId = split[0];
            CacheHelper.put(goodsId, split[1]);
        }

    }


}
