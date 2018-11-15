package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPaypointOrderHistory;

public interface YdPaypointOrderHistoryMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdPaypointOrderHistory record);

    int insertSelective(YdPaypointOrderHistory record);

    YdPaypointOrderHistory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(YdPaypointOrderHistory record);

    int updateByPrimaryKey(YdPaypointOrderHistory record);
}