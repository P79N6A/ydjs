package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPermission;

public interface YdPermissionMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdPermission record);

    int insertSelective(YdPermission record);

    YdPermission selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(YdPermission record);

    int updateByPrimaryKey(YdPermission record);
}