package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdWeixinUserInfo;
import org.apache.ibatis.annotations.Param;

public interface YdWeixinUserInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdWeixinUserInfo record);

    int insertSelective(YdWeixinUserInfo record);

    YdWeixinUserInfo selectByPrimaryKey(Long id);
    YdWeixinUserInfo selectByOpenid(@Param(value = "weixinConfigId") String weixinConfigId, @Param(value = "openid")String openid);
    YdWeixinUserInfo selectByUnionid(String unionid);
    YdWeixinUserInfo selectByMobile(@Param(value = "weixinConfigId") String weixinConfigId, @Param(value = "mobile")String mobile);
    YdWeixinUserInfo selectByOpenidLockRow(@Param(value = "weixinConfigId") String weixinConfigId, @Param(value = "openid")String openid);
    YdWeixinUserInfo selectByUnionidLockRow(String unionid);

    int updateByPrimaryKeySelective(YdWeixinUserInfo record);

    int updateByPrimaryKey(YdWeixinUserInfo record);
}