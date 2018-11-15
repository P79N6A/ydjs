package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPaypointShopInfo;

import java.util.List;

public interface YdPaypointShopInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdPaypointShopInfo record);

    int insertSelective(YdPaypointShopInfo record);

    YdPaypointShopInfo selectByPrimaryKey(Long id);

    YdPaypointShopInfo selectByShopId(String shopid);

    YdPaypointShopInfo selectByShopIdRowLock(String shopid);

    YdPaypointShopInfo selectByShopName(String name);

    List<YdPaypointShopInfo> selectAll();

    int updateByPrimaryKeySelective(YdPaypointShopInfo record);

    int updateByPrimaryKey(YdPaypointShopInfo record);
}