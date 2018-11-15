package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPaypointContractinfo;

public interface YdPaypointContractinfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdPaypointContractinfo record);

    int insertSelective(YdPaypointContractinfo record);

    YdPaypointContractinfo selectByPrimaryKey(Long id);
    YdPaypointContractinfo selectByContractId(String contractId);

    int updateByPrimaryKeySelective(YdPaypointContractinfo record);

    int updateByPrimaryKey(YdPaypointContractinfo record);
}