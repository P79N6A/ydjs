package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdShoporderLogistics;

public interface YdShoporderLogisticsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdShoporderLogistics record);

    int insertSelective(YdShoporderLogistics record);

    YdShoporderLogistics selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(YdShoporderLogistics record);

    int updateByPrimaryKey(YdShoporderLogistics record);
}