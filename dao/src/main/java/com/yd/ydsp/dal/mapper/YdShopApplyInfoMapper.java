package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdShopApplyInfo;

import java.util.List;

public interface YdShopApplyInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdShopApplyInfo record);

    int insertSelective(YdShopApplyInfo record);

    YdShopApplyInfo selectByPrimaryKey(Long id);
    YdShopApplyInfo selectByApplyid(String applyid);
    YdShopApplyInfo selectByApplyidRowLock(String applyid);
    List<YdShopApplyInfo> selectByAgentid(String agentid);
    List<YdShopApplyInfo> selectAll();

    int updateByPrimaryKeySelective(YdShopApplyInfo record);

    int updateByPrimaryKey(YdShopApplyInfo record);
}