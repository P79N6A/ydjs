package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdShopUserCashRecharge;
import org.apache.ibatis.annotations.Param;

public interface YdShopUserCashRechargeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdShopUserCashRecharge record);

    int insertSelective(YdShopUserCashRecharge record);

    YdShopUserCashRecharge selectByPrimaryKey(Long id);
    YdShopUserCashRecharge selectByBillId(@Param(value = "billid") String billid, @Param(value = "billtype")Integer billtype);

    int updateByPrimaryKeySelective(YdShopUserCashRecharge record);

    int updateByPrimaryKey(YdShopUserCashRecharge record);
}