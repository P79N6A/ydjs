package com.yd.ydsp.biz.openshop.impl;

import com.yd.ydsp.biz.cp.WareService;
import com.yd.ydsp.biz.opensearch.GoodsSearchService;
import com.yd.ydsp.biz.openshop.ShopGoodsService;
import com.yd.ydsp.client.domian.paypoint.WareItemVO;
import com.yd.ydsp.client.domian.paypoint.WareSkuSearcheVO;
import com.yd.ydsp.client.domian.paypoint.WareSkuVO;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.dal.entity.YdPaypointWaresSku;
import com.yd.ydsp.dal.mapper.YdPaypointWaresSkuMapper;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

public class ShopGoodsServiceImpl implements ShopGoodsService {

    public static final Logger logger = LoggerFactory.getLogger(ShopGoodsServiceImpl.class);

    @Resource
    private DozerBeanMapper doMapper;
    @Resource
    private WareService wareService;
    @Resource
    private GoodsSearchService goodsSearchService;
    @Resource
    private YdPaypointWaresSkuMapper ydPaypointWaresSkuMapper;

    @Override
    public boolean updateOpenSearchData(String skuid) {
        YdPaypointWaresSku waresSku = ydPaypointWaresSkuMapper.selectBySkuIdRowLock(skuid);
        if(waresSku==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), skuid+"商品id没找到!");
        }
        WareSkuVO wareSkuVO = wareService.getWareSku(waresSku);
        WareItemVO wareItemVO = wareService.getWareItem(waresSku.getWareitemid());
        WareSkuSearcheVO wareSkuSearcheVO = doMapper.map(wareSkuVO,WareSkuSearcheVO.class);
        wareSkuSearcheVO.setWareitemName(wareItemVO.getName());
        wareSkuSearcheVO.setWareitemStatus(wareItemVO.getStatus());
        if(wareItemVO==null){
            logger.error(waresSku.getWareitemid()+":商品类目id没有找到!");
            return true;
        }
        return goodsSearchService.commitOrderData(wareItemVO.getName(),wareSkuSearcheVO);
    }
}
