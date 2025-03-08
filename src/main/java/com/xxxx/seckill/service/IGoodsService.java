package com.xxxx.seckill.service;

import com.xxxx.seckill.pojo.Goods;
import com.xxxx.seckill.vo.GoodsVo;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;


/**
 * <p>
 *  获取商品详情
 * </p>
 *
 * 
 */
public interface IGoodsService extends IService<Goods> {

    /**
     * 获取商品列表
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 获取商品详情
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
    

}
