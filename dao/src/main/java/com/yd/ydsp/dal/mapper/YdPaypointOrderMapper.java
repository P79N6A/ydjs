package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPaypointOrder;

public interface YdPaypointOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdPaypointOrder record);

    int insertSelective(YdPaypointOrder record);

    YdPaypointOrder selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(YdPaypointOrder record);

    int updateByPrimaryKey(YdPaypointOrder record);
}