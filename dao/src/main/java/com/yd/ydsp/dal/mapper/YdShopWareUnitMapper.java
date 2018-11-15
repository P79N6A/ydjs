package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdShopWareUnit;

public interface YdShopWareUnitMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdShopWareUnit record);

    int insertSelective(YdShopWareUnit record);

    YdShopWareUnit selectByPrimaryKey(Long id);
    YdShopWareUnit selectByShopid(String shopid);
    YdShopWareUnit selectByShopidRowLock(String shopid);

    int updateByPrimaryKeySelective(YdShopWareUnit record);

    int updateByPrimaryKey(YdShopWareUnit record);
}