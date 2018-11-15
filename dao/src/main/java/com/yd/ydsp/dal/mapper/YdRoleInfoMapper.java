package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdRoleInfo;

public interface YdRoleInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdRoleInfo record);

    int insertSelective(YdRoleInfo record);

    YdRoleInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(YdRoleInfo record);

    int updateByPrimaryKey(YdRoleInfo record);
}