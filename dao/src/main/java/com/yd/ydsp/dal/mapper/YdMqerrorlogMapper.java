package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdMqerrorlog;

public interface YdMqerrorlogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdMqerrorlog record);

    int insertSelective(YdMqerrorlog record);

    YdMqerrorlog selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(YdMqerrorlog record);

    int updateByPrimaryKey(YdMqerrorlog record);
}