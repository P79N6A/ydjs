package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPaypointCpuserInfo;

public interface YdPaypointCpuserInfoMapper {
    int deleteByPrimaryKey(Long id);//selectByOpenid

    int insert(YdPaypointCpuserInfo record);

    int insertSelective(YdPaypointCpuserInfo record);

    YdPaypointCpuserInfo selectByPrimaryKey(Long id);
    YdPaypointCpuserInfo selectByUnionId(String unionid);
    YdPaypointCpuserInfo selectByUnionIdLockRow(String unionid);
    YdPaypointCpuserInfo selectByOpenid(String openid);
    YdPaypointCpuserInfo selectByOpenidLockRow(String openid);
    YdPaypointCpuserInfo selectByMobile(String mobile);
    YdPaypointCpuserInfo selectByMobileLockRow(String mobile);
    YdPaypointCpuserInfo selectByEmail(String email);

    int updateByPrimaryKeySelective(YdPaypointCpuserInfo record);

    int updateByPrimaryKey(YdPaypointCpuserInfo record);
}