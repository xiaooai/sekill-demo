package com.xxxx.seckill.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xxxx.seckill.pojo.Order;
import com.xxxx.seckill.pojo.SeckillMessage;
import com.xxxx.seckill.pojo.SeckillOrder;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.rabbitmq.MQSender;
import com.xxxx.seckill.service.IGoodsService;
import com.xxxx.seckill.service.IOrderService;
import com.xxxx.seckill.service.ISeckillOrderService;
import com.xxxx.seckill.utils.JsonUtil;
import com.xxxx.seckill.vo.GoodsVo;
import com.xxxx.seckill.vo.RespBean;
import com.xxxx.seckill.vo.RespBeanEnum;


@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean{

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MQSender mqSender;

    private Map<Long, Boolean> EMPTY_STOCK_MAP = new HashMap<>();

    // 秒杀：
    // windows优化前QPS： 530.8/sec
    // linux优化前QPS：1535.1/sec
    // 优化后QPS： 1661/sec
    // 
    @RequestMapping("/doSeckill2")
    public String doSeckill2(Model model, User user, Long goodsId) {
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        // 判断库存
        if (goods.getStockCount() < 1) {
            // 打印获取的库存
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }
        // // 判断是否重复秒杀
        // SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>()
        //         .eq("user_id", user.getId())
        //         .eq("goods_id", goodsId));
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        
        if (seckillOrder != null) {
            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            return "secKillFail";
        }
        // 下单
        Order order = orderService.seckill(user, goods);
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);
        return "orderDetail";
    }

    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(Model model, User user, Long goodsId) {
        if (user == null) {
            System.out.println("用户未登录");
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 判断是否重复秒杀
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
            System.out.println("用户:" + user.getId() + "重复秒杀商品:" + goodsId);
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }
        
        // 修改内存标记判断逻辑
        if (EMPTY_STOCK_MAP.getOrDefault(goodsId, false)) {
            System.out.println("商品ID:" + goodsId + " 内存标记为空");
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        // 预减库存
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        System.out.println("商品ID:" + goodsId + " Redis预减后库存:" + stock);
        
        if (stock < 0) {
            EMPTY_STOCK_MAP.put(goodsId, true);
            valueOperations.increment("seckillGoods:" + goodsId);
            System.out.println("商品ID:" + goodsId + " 库存不足，Redis库存回滚");
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // 下单
        try {
            System.out.println("开始发送秒杀消息，用户ID：" + user.getId() + "，商品ID：" + goodsId);
            SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
            mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
            System.out.println("秒杀消息发送成功");
            return RespBean.success(0);
        } catch (Exception e) {
            System.out.println("发送秒杀消息失败：" + e.getMessage());
            e.printStackTrace();
            return RespBean.error(RespBeanEnum.ERROR);
        }
    }

    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    // 获取秒杀结果, 返回订单id.
    // 如果秒杀成功，返回订单id，如果秒杀失败，返回-1; 排队中，返回0
    public RespBean getResult(User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        try {
            System.out.println("查询秒杀结果，用户ID：" + user.getId() + "，商品ID：" + goodsId);
            Long orderId = seckillOrderService.getResult(user, goodsId);
            System.out.println("查询结果，订单ID：" + orderId);
            return RespBean.success(orderId);
        } catch (Exception e) {
            System.out.println("查询秒杀结果失败：" + e.getMessage());
            e.printStackTrace();
            return RespBean.error(RespBeanEnum.ERROR);
        }
    }



    
    // 系统初始化，把商品库存信息存入redis
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.findGoodsVo();
        if(CollectionUtils.isEmpty(goodsList)) {
            System.out.println("初始化：商品列表为空");
            return;
        }
        for (GoodsVo goodsVo : goodsList) {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            EMPTY_STOCK_MAP.put(goodsVo.getId(), false);
            System.out.println("初始化：商品ID:" + goodsVo.getId() + 
                             " 初始库存:" + goodsVo.getStockCount() + 
                             " Redis库存:" + redisTemplate.opsForValue().get("seckillGoods:" + goodsVo.getId()));
        }
    }
}
