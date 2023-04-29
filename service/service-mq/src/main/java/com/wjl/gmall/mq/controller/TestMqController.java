package com.wjl.gmall.mq.controller;

import com.wjl.gmall.common.config.demo.DeadLetterMQConfig;
import com.wjl.gmall.common.config.demo.MqDelayedConfig;
import com.wjl.gmall.common.result.Result;
import com.wjl.gmall.common.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/4/29
 * @description
 */
@RequestMapping("test")
@RestController
public class TestMqController {

    @Autowired
    private RabbitService rabbitService;

    @GetMapping("send/{message}")
    public Result send(@PathVariable String message) {
        rabbitService.sendMessage("test.exchange", "test.key", message);
        return Result.ok();
    }

    @GetMapping("sendDead/{message}")
    public Result sendDead(@PathVariable String message) {
        rabbitService.sendMessage(DeadLetterMQConfig.exchange_dead, DeadLetterMQConfig.routing_dead_1, message);
        System.out.println(LocalDateTime.now());
        return Result.ok();
    }

    @GetMapping("sendDelay/{message}")
    public Result sendDelay(@PathVariable String message) {
        System.out.println("sendDelayMessage:" + message + " time:" + LocalDateTime.now());
        rabbitService.sendDelayMessage(MqDelayedConfig.exchange_delay, MqDelayedConfig.routing_delay, message, 10, TimeUnit.SECONDS);
        return Result.ok();
    }
}
