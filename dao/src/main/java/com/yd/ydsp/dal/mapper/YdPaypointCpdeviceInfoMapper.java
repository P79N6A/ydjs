package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPaypointCpdeviceInfo;
import org.apache.ibatis.annotations.Param;

public interface YdPaypointCpdeviceInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdPaypointCpdeviceInfo record);

    int insertSelective(YdPaypointCpdeviceInfo record);

    YdPaypointCpdeviceInfo selectByPrimaryKey(Long id);

    YdPaypointCpdeviceInfo selectByDeviceId(String deviceid);

    YdPaypointCpdeviceInfo selectBySN(@Param(value = "sn") String sn,@Param(value = "deviceType")String deviceType);

    int updateByPrimaryKeySelective(YdPaypointCpdeviceInfo record);

    int updateByPrimaryKey(YdPaypointCpdeviceInfo record);
}