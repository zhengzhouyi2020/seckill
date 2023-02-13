package com.zzy.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzy.seckill.entity.Goods;
import com.zzy.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zzy
 * @since 2022-01-26
 */
public interface GoodsMapper extends BaseMapper<Goods> {


    List<GoodsVo> findGoodVo();

    GoodsVo findGoodVoByGoodsId(Long goodsId);
}
