package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdCpChannel;

import java.util.List;

public interface YdCpChannelMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdCpChannel record);

    int insertSelective(YdCpChannel record);

    YdCpChannel selectByPrimaryKey(Long id);
    List<YdCpChannel> selectByShopid(String shopid);
    YdCpChannel selectByChannelid(String channelid);

    int updateByPrimaryKeySelective(YdCpChannel record);

    int updateByPrimaryKey(YdCpChannel record);
}