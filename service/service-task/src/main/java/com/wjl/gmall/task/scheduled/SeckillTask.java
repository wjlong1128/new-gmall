package com.wjl.gmall.task.scheduled;

import com.wjl.gmall.common.constants.MqConst;
import com.wjl.gmall.common.service.RabbitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/3
 * @description 秒杀活动
 */
@EnableScheduling
@Component
public class SeckillTask {
    private static Logger log = LoggerFactory.getLogger(SeckillTask.class);
    @Autowired
    private RabbitService rabbitService;

    /**
     * 定时发送消息提醒上架秒杀商品
     */
    @Scheduled(cron = "0/3 * * * * ?")
    public void seckillSku() {
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK, MqConst.ROUTING_TASK_1, 1L);
    }

    ///**
    // * * 任何时间
    // * ? 日和星期
    // */
    //@Scheduled(cron = "0 0 1 * * *")
    //public void testTask1() {
    //    System.out.println("执行");
    //}


}
