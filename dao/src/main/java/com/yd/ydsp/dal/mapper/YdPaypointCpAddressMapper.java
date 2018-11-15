package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPaypointCpAddress;

public interface YdPaypointCpAddressMapper {
    int deleteByPrimaryKey(Long id);

    int deleteByAddressId(String addressid);

    int insert(YdPaypointCpAddress record);

    int insertSelective(YdPaypointCpAddress record);

    YdPaypointCpAddress selectByPrimaryKey(Long id);

    YdPaypointCpAddress selectByAddressid(String addressid);

    int updateByPrimaryKeySelective(YdPaypointCpAddress record);

    int updateByPrimaryKey(YdPaypointCpAddress record);
}