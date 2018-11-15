package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPaypointConsumerAddress;

import java.util.List;

public interface YdPaypointConsumerAddressMapper {
    int deleteByPrimaryKey(Long id);

    int deleteByAddressId(String addressid);

    int insert(YdPaypointConsumerAddress record);

    int insertSelective(YdPaypointConsumerAddress record);

    YdPaypointConsumerAddress selectByPrimaryKey(Long id);
    YdPaypointConsumerAddress selectByAddressid(String addressid);
    List<YdPaypointConsumerAddress> selectByUnionId(String unionid);

    int updateByPrimaryKeySelective(YdPaypointConsumerAddress record);

    int updateByPrimaryKey(YdPaypointConsumerAddress record);
}