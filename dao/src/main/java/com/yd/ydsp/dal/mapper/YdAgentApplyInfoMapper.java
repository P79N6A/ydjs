package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdAgentApplyInfo;

import java.util.List;

public interface YdAgentApplyInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdAgentApplyInfo record);

    int insertSelective(YdAgentApplyInfo record);

    YdAgentApplyInfo selectByPrimaryKey(Long id);
    YdAgentApplyInfo selectByOpenid(String openid);
    YdAgentApplyInfo selectByAgentid(String agentid);
    YdAgentApplyInfo selectByAgentidRowLock(String agentid);

    List<YdAgentApplyInfo> selectAll();

    int updateByPrimaryKeySelective(YdAgentApplyInfo record);

    int updateByPrimaryKey(YdAgentApplyInfo record);
}