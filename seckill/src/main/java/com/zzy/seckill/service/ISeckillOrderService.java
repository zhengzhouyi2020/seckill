package com.zzy.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzy.seckill.entity.OrderInfo;
import com.zzy.seckill.entity.SeckillOrder;
import com.zzy.seckill.entity.User;
import com.zzy.seckill.vo.GoodsVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jy
 * @since 2022-01-26
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    OrderInfo seckill(User user, GoodsVo goods);

    Long getResult(User user, Long goodsId);

    String createPath(User user, Long goodsId);

    Boolean checkPath(User user, Long goodsId, String path);
}
