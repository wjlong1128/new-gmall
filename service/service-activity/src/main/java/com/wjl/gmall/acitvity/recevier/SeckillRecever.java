package com.wjl.gmall.acitvity.recevier;

import com.rabbitmq.client.Channel;
import com.wjl.gmall.acitvity.model.entity.UserRecode;
import com.wjl.gmall.acitvity.service.SeckillService;
import com.wjl.gmall.common.constants.MqConst;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author wangjianlong
 * @version 1.0.0
 * @date 2023/5/3
 * @description
 */
@Component
public class SeckillRecever {

    @Autowired
    private SeckillService seckillService;

    /**
     * 导入商品到redis预热
     *
     * @param day
     * @param message
     * @param channel
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = MqConst.QUEUE_TASK_1),
                    exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_TASK),
                    key = MqConst.ROUTING_TASK_1
            )
    )
    public void importToRedis(Integer day, Message message, Channel channel) throws IOException {
        if (day == null || day < 0L) {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }
        // 预热数据
        this.seckillService.preheatSeckill(day);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    /**
     * 处理用户成功秒杀抢单排队
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = MqConst.QUEUE_SECKILL_USER),
                    exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_SECKILL_USER),
                    key = MqConst.ROUTING_SECKILL_USER
            )
    )
    public void seckillOrder(UserRecode userRecode, Message message, Channel channel) throws IOException {
        this.seckillService.seckillOrderProcess(userRecode.getUserId(), userRecode.getSkuId());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = MqConst.QUEUE_TASK_18),
                    exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_TASK),
                    key = MqConst.ROUTING_TASK_18
            )
    )
    public void clearSeckill(Message message, Channel channel) throws IOException {
this.seckillService.clearOverDueSeckill();
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
