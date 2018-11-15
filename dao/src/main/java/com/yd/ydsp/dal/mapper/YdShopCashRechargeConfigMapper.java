package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdShopCashRechargeConfig;

public interface YdShopCashRechargeConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdShopCashRechargeConfig record);

    int insertSelective(YdShopCashRechargeConfig record);

    YdShopCashRechargeConfig selectByPrimaryKey(Long id);
    YdShopCashRechargeConfig selectByShopid(String shopid);
    YdShopCashRechargeConfig selectByShopidRowLock(String shopid);

    int updateByPrimaryKeySelective(YdShopCashRechargeConfig record);

    int updateByPrimaryKey(YdShopCashRechargeConfig record);
}