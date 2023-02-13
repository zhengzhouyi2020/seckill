package com.zzy.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzy.seckill.entity.OrderInfo;
import com.zzy.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zzy
 * @since 2022-01-26
 */
public interface IOrderInfoService extends IService<OrderInfo> {

    OrderDetailVo findDetailById(Long orderId);
}
