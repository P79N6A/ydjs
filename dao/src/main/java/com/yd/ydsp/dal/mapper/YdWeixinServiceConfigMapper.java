package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdWeixinServiceConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YdWeixinServiceConfigMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdWeixinServiceConfig record);

    int insertSelective(YdWeixinServiceConfig record);

    YdWeixinServiceConfig selectByPrimaryKey(Long id);
    YdWeixinServiceConfig selectByAppid(String appid);
    YdWeixinServiceConfig selectByWeixinConfigId(String weixinConfigId);
    YdWeixinServiceConfig selectByShopIdAndType(@Param(value = "shopid") String shopid, @Param(value = "weixinType") Integer weixinType);
    List<YdWeixinServiceConfig> selectByShopId(@Param(value = "shopid") String shopid);

    int updateByPrimaryKeySelective(YdWeixinServiceConfig record);

    int updateByPrimaryKey(YdWeixinServiceConfig record);
}