package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPrintOrderLog;

public interface YdPrintOrderLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdPrintOrderLog record);

    int insertSelective(YdPrintOrderLog record);

    YdPrintOrderLog selectByPrimaryKey(Long id);

    YdPrintOrderLog selectByOrderid(String orderid);

    int updateByPrimaryKeySelective(YdPrintOrderLog record);

    int updateByPrimaryKey(YdPrintOrderLog record);
}