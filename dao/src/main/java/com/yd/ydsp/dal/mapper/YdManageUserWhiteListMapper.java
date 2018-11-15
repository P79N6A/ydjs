package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdManageUserWhiteList;

public interface YdManageUserWhiteListMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdManageUserWhiteList record);

    int insertSelective(YdManageUserWhiteList record);

    YdManageUserWhiteList selectByPrimaryKey(Long id);
    YdManageUserWhiteList selectByMobile(String mobile);

    int updateByPrimaryKeySelective(YdManageUserWhiteList record);

    int updateByPrimaryKey(YdManageUserWhiteList record);
}