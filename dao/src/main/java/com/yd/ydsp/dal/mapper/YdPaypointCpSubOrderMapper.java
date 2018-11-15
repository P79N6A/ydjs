package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPaypointCpSubOrder;

import java.util.List;

public interface YdPaypointCpSubOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdPaypointCpSubOrder record);

    int insertSelective(YdPaypointCpSubOrder record);

    YdPaypointCpSubOrder selectByPrimaryKey(Long id);

    List<YdPaypointCpSubOrder> selectByOrderId(String orderid);

    YdPaypointCpSubOrder selectBySubOrderId(String subOrderid);

    int updateByPrimaryKeySelective(YdPaypointCpSubOrder record);

    int updateByPrimaryKey(YdPaypointCpSubOrder record);
}