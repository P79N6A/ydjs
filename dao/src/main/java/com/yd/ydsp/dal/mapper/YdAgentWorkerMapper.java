package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdAgentWorker;

public interface YdAgentWorkerMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdAgentWorker record);

    int insertSelective(YdAgentWorker record);

    YdAgentWorker selectByPrimaryKey(Long id);
    YdAgentWorker selectByAgentId(String agentid);
    YdAgentWorker selectByAgentIdRowLock(String agentid);

    int updateByPrimaryKeySelective(YdAgentWorker record);

    int updateByPrimaryKey(YdAgentWorker record);
}