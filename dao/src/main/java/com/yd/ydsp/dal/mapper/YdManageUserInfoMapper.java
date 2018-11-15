package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdManageUserInfo;

public interface YdManageUserInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdManageUserInfo record);

    int insertSelective(YdManageUserInfo record);

    YdManageUserInfo selectByPrimaryKey(Long id);
    YdManageUserInfo selectByUnionId(String unionid);
    YdManageUserInfo selectByUnionIdLockRow(String unionid);
    YdManageUserInfo selectByOpenid(String openid);
    YdManageUserInfo selectByOpenidLockRow(String openid);
    YdManageUserInfo selectByMobile(String mobile);
    YdManageUserInfo selectByMobileLockRow(String mobile);

    int updateByPrimaryKeySelective(YdManageUserInfo record);

    int updateByPrimaryKey(YdManageUserInfo record);
}