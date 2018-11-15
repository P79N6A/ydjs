package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPayYeepayCallback;

public interface YdPayYeepayCallbackMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdPayYeepayCallback record);

    int insertSelective(YdPayYeepayCallback record);

    YdPayYeepayCallback selectByPrimaryKey(Long id);

    YdPayYeepayCallback selectByYbOrderId(Long yborderid);

    YdPayYeepayCallback selectByOrderId(String orderid);

    int updateByPrimaryKeySelective(YdPayYeepayCallback record);

    int updateByPrimaryKey(YdPayYeepayCallback record);
}