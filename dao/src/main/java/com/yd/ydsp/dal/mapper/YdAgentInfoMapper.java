package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdAgentInfo;

import java.util.List;

public interface YdAgentInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdAgentInfo record);

    int insertSelective(YdAgentInfo record);

    YdAgentInfo selectByPrimaryKey(Long id);
    YdAgentInfo selectByOpenid(String openid);
    YdAgentInfo selectByAgentid(String agentid);
    YdAgentInfo selectByAgentidRowLock(String agentid);
    List<YdAgentInfo> selectAll();

    int updateByPrimaryKeySelective(YdAgentInfo record);

    int updateByPrimaryKey(YdAgentInfo record);
}