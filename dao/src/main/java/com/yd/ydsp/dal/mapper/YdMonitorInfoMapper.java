package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdMonitorInfo;

public interface YdMonitorInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdMonitorInfo record);

    int insertSelective(YdMonitorInfo record);

    YdMonitorInfo selectByPrimaryKey(Long id);

    YdMonitorInfo selectByShopid(String shopid);

    YdMonitorInfo selectByShopidLowRow(String shopid);

    int updateByPrimaryKeySelective(YdMonitorInfo record);

    int updateByPrimaryKey(YdMonitorInfo record);
}