package com.zzy.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzy.seckill.mapper.GoodsMapper;
import com.zzy.seckill.entity.Goods;
import com.zzy.seckill.service.IGoodsService;
import com.zzy.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jy
 * @since 2022-01-26
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Override
    public List<GoodsVo> findGoodVo() {

        return goodsMapper.findGoodVo();
    }

    @Override
    public GoodsVo findGoodVoByGoodsId(Long goodsId) {
        return goodsMapper.findGoodVoByGoodsId(goodsId);
    }

}
