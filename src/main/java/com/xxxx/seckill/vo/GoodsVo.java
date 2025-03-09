package com.xxxx.seckill.vo;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.xxxx.seckill.pojo.Goods;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsVo extends Goods {
    
    private BigDecimal seckillPrice;

    
    private Integer stockCount;

    
    private Date startDate;

    
    private Date endDate;

}
