package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPrintCashierOrderLog;

public interface YdPrintCashierOrderLogMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdPrintCashierOrderLog record);

    int insertSelective(YdPrintCashierOrderLog record);

    YdPrintCashierOrderLog selectByPrimaryKey(Long id);

    YdPrintCashierOrderLog selectByOrderid(String orderid);

    int updateByPrimaryKeySelective(YdPrintCashierOrderLog record);

    int updateByPrimaryKey(YdPrintCashierOrderLog record);
}