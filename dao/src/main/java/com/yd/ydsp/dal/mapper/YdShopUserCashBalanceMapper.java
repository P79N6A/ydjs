package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdShopUserCashBalance;
import org.apache.ibatis.annotations.Param;

public interface YdShopUserCashBalanceMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdShopUserCashBalance record);

    int insertSelective(YdShopUserCashBalance record);

    YdShopUserCashBalance selectByPrimaryKey(Long id);

    YdShopUserCashBalance selectByUserIdAndShopId(@Param(value = "userid") Long userid,@Param(value = "shopid") String shopid);
    YdShopUserCashBalance selectByUserIdAndShopIdRowLock(@Param(value = "userid") Long userid,@Param(value = "shopid") String shopid);

    int updateByPrimaryKeySelective(YdShopUserCashBalance record);

    int updateByPrimaryKey(YdShopUserCashBalance record);
}