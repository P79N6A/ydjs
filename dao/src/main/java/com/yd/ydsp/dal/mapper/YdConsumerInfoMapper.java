package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdConsumerInfo;

public interface YdConsumerInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdConsumerInfo record);

    int insertSelective(YdConsumerInfo record);

    YdConsumerInfo selectByPrimaryKey(Long id);
    YdConsumerInfo selectByUnionId(String unionid);
    YdConsumerInfo selectByUnionIdLockRow(String unionid);
    YdConsumerInfo selectByOpenid(String openid);
    YdConsumerInfo selectByOpenidLockRow(String openid);

    int updateByPrimaryKeySelective(YdConsumerInfo record);

    int updateByPrimaryKey(YdConsumerInfo record);
}