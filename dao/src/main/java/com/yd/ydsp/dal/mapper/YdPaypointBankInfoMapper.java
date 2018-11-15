package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPaypointBankInfo;
import org.apache.ibatis.annotations.Param;

public interface YdPaypointBankInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdPaypointBankInfo record);

    int insertSelective(YdPaypointBankInfo record);

    YdPaypointBankInfo selectByPrimaryKey(Long id);

    YdPaypointBankInfo selectByOpenidAndShopid(@Param(value = "openid") String openid,@Param(value = "shopid") String shopid);

    int updateByPrimaryKeySelective(YdPaypointBankInfo record);

    int updateByPrimaryKey(YdPaypointBankInfo record);
}