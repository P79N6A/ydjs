package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPaypointShopInfoExt;

import java.util.List;

public interface YdPaypointShopInfoExtMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdPaypointShopInfoExt record);

    int insertSelective(YdPaypointShopInfoExt record);

    YdPaypointShopInfoExt selectByPrimaryKey(Long id);
    YdPaypointShopInfoExt selectByShopId(String shopid);
    YdPaypointShopInfoExt selectByWeixinConfigId(String weixinConfigId);
    List<YdPaypointShopInfoExt> selectByAgentId(String agentid);
    List<YdPaypointShopInfoExt> selectAll();

    int updateByPrimaryKeySelective(YdPaypointShopInfoExt record);

    int updateByPrimaryKey(YdPaypointShopInfoExt record);
}