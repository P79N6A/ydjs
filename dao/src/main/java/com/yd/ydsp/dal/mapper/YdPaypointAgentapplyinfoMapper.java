package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPaypointAgentapplyinfo;

import java.util.List;

public interface YdPaypointAgentapplyinfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdPaypointAgentapplyinfo record);

    int insertSelective(YdPaypointAgentapplyinfo record);

    YdPaypointAgentapplyinfo selectByPrimaryKey(Long id);
    List<YdPaypointAgentapplyinfo> selectAll();

    int updateByPrimaryKeySelective(YdPaypointAgentapplyinfo record);

    int updateByPrimaryKey(YdPaypointAgentapplyinfo record);
}