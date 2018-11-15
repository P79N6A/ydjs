package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdCpSpecInfo;

import java.util.List;

public interface YdCpSpecInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdCpSpecInfo record);

    int insertSelective(YdCpSpecInfo record);

    YdCpSpecInfo selectByPrimaryKey(Long id);
    YdCpSpecInfo selectBySpecid(String specid);
    List<YdCpSpecInfo> selectByShopid(String shopid);

    int updateByPrimaryKeySelective(YdCpSpecInfo record);

    int updateByPrimaryKey(YdCpSpecInfo record);
}