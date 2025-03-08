package com.xxxx.seckill.service;

import com.xxxx.seckill.pojo.Order;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.vo.GoodsVo;
import com.xxxx.seckill.vo.OrderDetailVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2025-02-26
 */
public interface IOrderService extends IService<Order> {

    /**
     * 秒杀
     * 
     */
    Order seckill(User user, GoodsVo goods);


    // 订单详情
    OrderDetailVo detail(Long orderId);


}
