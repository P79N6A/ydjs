package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPaypointWaresSkuExt;

public interface YdPaypointWaresSkuExtMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdPaypointWaresSkuExt record);

    int insertSelective(YdPaypointWaresSkuExt record);

    YdPaypointWaresSkuExt selectByPrimaryKey(Long id);
    YdPaypointWaresSkuExt selectBySkuId(String skuid);

    int updateByPrimaryKeySelective(YdPaypointWaresSkuExt record);

    int updateByPrimaryKey(YdPaypointWaresSkuExt record);
}