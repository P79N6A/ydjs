package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdShopUserData;
import org.apache.ibatis.annotations.Param;

public interface YdShopUserDataMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdShopUserData record);

    int insertSelective(YdShopUserData record);

    YdShopUserData selectByPrimaryKey(Long id);
    YdShopUserData selectByUserId(@Param("userid") Long userid,@Param("userSource") Integer userSource, @Param("shopid") String shopid);
    YdShopUserData selectByUserIdRowLock(@Param("userid") Long userid,@Param("userSource") Integer userSource, @Param("shopid") String shopid);

    int updateByPrimaryKeySelective(YdShopUserData record);

    int updateByPrimaryKey(YdShopUserData record);
}