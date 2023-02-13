package com.zzy.seckill.vo;

import com.zzy.seckill.entity.OrderInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by zzy
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVo {
    private GoodsVo goodsVo;
    private OrderInfo order;

}
