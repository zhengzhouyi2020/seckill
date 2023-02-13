package com.zzy.seckill.vo;


import com.zzy.seckill.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by zzy.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsDetailVo {
    private int seckillStatus = 0;
    private int remainSeconds = 0;
    private GoodsVo goods ;
    private User user;

}
