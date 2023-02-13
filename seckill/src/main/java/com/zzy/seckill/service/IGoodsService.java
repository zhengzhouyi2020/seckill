package com.zzy.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzy.seckill.entity.Goods;
import com.zzy.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zzy
 * @since 2022-01-26
 */
public interface IGoodsService extends IService<Goods> {

    List<GoodsVo> findGoodVo();

    GoodsVo findGoodVoByGoodsId(Long goodsId);
}
