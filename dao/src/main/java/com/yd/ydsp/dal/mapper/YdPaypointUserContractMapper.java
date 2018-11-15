package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPaypointUserContract;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YdPaypointUserContractMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdPaypointUserContract record);

    int insertSelective(YdPaypointUserContract record);

    YdPaypointUserContract selectByPrimaryKey(Long id);
    List<YdPaypointUserContract> selectByOpenid(String openid);
    YdPaypointUserContract selectByOpenidAndContractId(@Param(value = "openid") String openid,
                                                       @Param(value = "contractId") String contractId);

    YdPaypointUserContract selectByOpenidAndContractIdAndShopid(@Param(value = "openid") String openid,
                                                       @Param(value = "contractId") String contractId,
                                                       @Param(value = "shopid") String shopid);

    int updateByPrimaryKeySelective(YdPaypointUserContract record);

    int updateByPrimaryKey(YdPaypointUserContract record);
}