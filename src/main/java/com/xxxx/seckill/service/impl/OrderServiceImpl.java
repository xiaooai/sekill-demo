package com.xxxx.seckill.service.impl;

import com.xxxx.seckill.pojo.Order;
import com.xxxx.seckill.pojo.SeckillGoods;
import com.xxxx.seckill.pojo.SeckillOrder;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.exception.GlobalException;
import com.xxxx.seckill.mapper.OrderMapper;
import com.xxxx.seckill.service.IGoodsService;
import com.xxxx.seckill.service.IOrderService;
import com.xxxx.seckill.service.ISeckillGoodsService;
import com.xxxx.seckill.service.ISeckillOrderService;
import com.xxxx.seckill.vo.GoodsVo;
import com.xxxx.seckill.vo.OrderDetailVo;
import com.xxxx.seckill.vo.RespBeanEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2025-02-26
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ISeckillGoodsService seckillGoodsService;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private IGoodsService goodsService;
    
    @Autowired
    private RedisTemplate redisTemplate;

    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goods) {
        try {
            ValueOperations valueOperations = redisTemplate.opsForValue();
            // 减库存
            SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
            System.out.println("商品ID:" + goods.getId() + " 数据库当前库存:" + seckillGoods.getStockCount());
            
            boolean result = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>()
                .setSql("stock_count = stock_count - 1")
                .eq("goods_id", goods.getId())
                .gt("stock_count", 0));
            
            System.out.println("商品ID:" + goods.getId() + " 数据库更新结果:" + result);
            
            if (!result) {
                valueOperations.set("isStockEmpty:" + goods.getId(), 0);
                System.out.println("商品ID:" + goods.getId() + " 数据库更新失败，设置空库存标记");
                return null;
            }

            // 生成订单
            try {
                Order order = new Order();
                order.setUserId(user.getId());
                order.setGoodsId(goods.getId());
                order.setDeliveryAddrId(0L);
                order.setGoodsName(goods.getGoodsName());
                order.setGoodsCount(1);
                order.setGoodsPrice(goods.getSeckillPrice());
                order.setOrderChannel(1);
                order.setStatus(0);
                order.setCreateDate(new Date());
                
                System.out.println("开始创建订单，订单信息：" + order);
                int insertResult = orderMapper.insert(order);
                System.out.println("订单创建结果：" + insertResult + "，订单ID：" + order.getId());
                
                // 生成秒杀订单
                try {
                    SeckillOrder seckillOrder = new SeckillOrder();
                    seckillOrder.setUserId(user.getId());
                    seckillOrder.setOrderId(order.getId());
                    seckillOrder.setGoodsId(goods.getId());
                    
                    System.out.println("开始创建秒杀订单，秒杀订单信息：" + seckillOrder);
                    boolean seckillOrderResult = seckillOrderService.save(seckillOrder);
                    System.out.println("秒杀订单创建结果：" + seckillOrderResult + "，秒杀订单ID：" + seckillOrder.getId());
                    
                    // 将秒杀订单存入Redis
                    redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goods.getId(), seckillOrder);
                    System.out.println("秒杀订单已存入Redis");
                    
                    return order;
                } catch (Exception e) {
                    System.out.println("创建秒杀订单失败：" + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
            } catch (Exception e) {
                System.out.println("创建订单失败：" + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        } catch (Exception e) {
            System.out.println("秒杀操作整体失败：" + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public OrderDetailVo detail(Long orderId) {
        if (orderId == null) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        // 获取订单信息
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        // 获取商品信息
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        if (goodsVo == null) {
            throw new GlobalException(RespBeanEnum.GOODS_NOT_EXIST);
        }
        
        OrderDetailVo detail = new OrderDetailVo();
        detail.setOrder(order);
        detail.setGoodsVo(goodsVo);
        return detail;
    }
}

