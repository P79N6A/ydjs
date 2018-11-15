package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdCpParameterInfo;

import java.util.List;

public interface YdCpParameterInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdCpParameterInfo record);

    int insertSelective(YdCpParameterInfo record);

    YdCpParameterInfo selectByPrimaryKey(Long id);
    YdCpParameterInfo selectByParameterid(String parameterid);
    List<YdCpParameterInfo> selectBySkuid(String skuid);
    List<YdCpParameterInfo> selectByShopid(String shopid);

    int updateByPrimaryKeySelective(YdCpParameterInfo record);

    int updateByPrimaryKey(YdCpParameterInfo record);
}