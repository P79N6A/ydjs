package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPaypointShopworker;

public interface YdPaypointShopworkerMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdPaypointShopworker record);

    int insertSelective(YdPaypointShopworker record);

    YdPaypointShopworker selectByPrimaryKey(Long id);
    YdPaypointShopworker selectByShopid(String shopid);
    YdPaypointShopworker selectByShopidLockRow(String shopid);

    int updateByPrimaryKeySelective(YdPaypointShopworker record);

    int updateByPrimaryKey(YdPaypointShopworker record);
}