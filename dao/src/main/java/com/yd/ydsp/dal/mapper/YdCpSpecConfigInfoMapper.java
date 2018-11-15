package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdCpSpecConfigInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YdCpSpecConfigInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdCpSpecConfigInfo record);

    int insertSelective(YdCpSpecConfigInfo record);

    YdCpSpecConfigInfo selectByPrimaryKey(Long id);
    List<YdCpSpecConfigInfo> selectBySkuid (String skuid);
    YdCpSpecConfigInfo selectBySkuidAndMainSpecName (@Param(value = "skuid")String skuid, @Param(value = "mainSpecName")String mainSpecName);
    YdCpSpecConfigInfo selectBySkuidAndMainSpecNameRowLock (@Param(value = "skuid")String skuid, @Param(value = "mainSpecName")String mainSpecName);
    List<YdCpSpecConfigInfo> selectByShopid (String shopid);

    int updateByPrimaryKeySelective(YdCpSpecConfigInfo record);

    int updateByPrimaryKey(YdCpSpecConfigInfo record);
}