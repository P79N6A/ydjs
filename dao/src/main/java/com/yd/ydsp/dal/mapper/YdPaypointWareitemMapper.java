package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPaypointWareitem;

import java.util.List;

public interface YdPaypointWareitemMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdPaypointWareitem record);

    int insertSelective(YdPaypointWareitem record);

    YdPaypointWareitem selectByPrimaryKey(Long id);
    YdPaypointWareitem selectByItemId(String itemid);

    List<YdPaypointWareitem> selectByShopid(String shopid);

    int updateByPrimaryKeySelective(YdPaypointWareitem record);

    int updateByPrimaryKey(YdPaypointWareitem record);
}