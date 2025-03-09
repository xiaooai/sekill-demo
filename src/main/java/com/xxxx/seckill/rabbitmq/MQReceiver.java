package com.xxxx.seckill.rabbitmq;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.xxxx.seckill.pojo.SeckillMessage;
import com.xxxx.seckill.pojo.SeckillOrder;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.service.IGoodsService;
import com.xxxx.seckill.service.IOrderService;
import com.xxxx.seckill.utils.JsonUtil;
import com.xxxx.seckill.vo.GoodsVo;
import com.xxxx.seckill.vo.RespBean;

import lombok.extern.slf4j.Slf4j;

// 消息消费者

@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IOrderService orderService;

 

    // @RabbitListener(queues = "queue")
    // public void receive(String msg) {
    //     log.info("receive message: {}", msg);
    // }

    // @RabbitListener(queues = "queue_fanout_01")
    // public void receive01(String msg) {
    //     log.info("QUEUE01 receive message: {}", msg);
    // }

    // @RabbitListener(queues = "queue_fanout_02")
    // public void receive02(String msg) {
    //     log.info("QUEUE02 receive message: {}", msg);
    // }

    // @RabbitListener(queues = "queue_direct_01")
    // public void receive03(String msg) {
    //     log.info("QUEUE01 receive message: {}", msg);
    // }

    // @RabbitListener(queues = "queue_direct_02")
    // public void receive04(String msg) {
    //     log.info("QUEUE02 receive message: {}", msg);
    // }

    // @RabbitListener(queues = "queue_topic_01")
    // public void receive05(String msg) {
    //     log.info("QUEUE01 receive message: {}", msg);
    // }   

    // @RabbitListener(queues = "queue_topic_02")
    // public void receive06(String msg) {
    //     log.info("QUEUE02 receive message: {}", msg);
    // }

    // @RabbitListener(queues = "queue_header_01")
    // public void receive07(Message msg) {
    //     log.info("QUEUE01 receive message object: {}", msg);
    //     log.info("QUEUE01 receive message: {}", new String(msg.getBody()));
    // }

    // @RabbitListener(queues = "queue_header_02")
    // public void receive08(Message msg) {
    //     log.info("QUEUE02 receive message: {}", msg);
    //     log.info("QUEUE02 receive message: {}", new String(msg.getBody()));
    // }




    @RabbitListener(queues = "seckillQueue")
    public void receive(String message) {
        log.info("receive seckill message: {}", message);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
        Long goodsId = seckillMessage.getGoodsId();
        User user = seckillMessage.getUser();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        // 判断库存
        if (goodsVo.getStockCount() < 1) {    
            return;
        }
        // 判断是否重复秒杀
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
            return;
        }
        // 下单
        orderService.seckill(user, goodsVo);
    }   

}
