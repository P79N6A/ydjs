package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdShopActivityInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YdShopActivityInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdShopActivityInfo record);

    int insertSelective(YdShopActivityInfo record);

    YdShopActivityInfo selectByPrimaryKey(Long id);

    YdShopActivityInfo selectByActivityid(String activityid);
    YdShopActivityInfo selectByActivityidRowLock(String activityid);

    List<YdShopActivityInfo> selectByShopidInYear(@Param(value = "shopid") String shopid,
                                                  @Param(value = "yearInfo")String yearInfo,
                                                  @Param(value = "indexPoint")Integer indexPoint,
                                                  @Param(value = "count")Integer count);
    List<YdShopActivityInfo> selectByShopidInMoth(@Param(value = "shopid") String shopid,
                                                  @Param(value = "yearInfo")String yearInfo,
                                                  @Param(value = "monthInfo")String monthInfo,
                                                  @Param(value = "indexPoint")Integer indexPoint,
                                                  @Param(value = "count")Integer count);

    List<YdShopActivityInfo> selectByShopidInYearStatus(@Param(value = "shopid") String shopid,
                                                  @Param(value = "yearInfo")String yearInfo,
                                                    @Param(value = "status") Integer status,
                                                    @Param(value = "indexPoint")Integer indexPoint,
                                                    @Param(value = "count")Integer count);
    List<YdShopActivityInfo> selectByShopidInMonthStatus(@Param(value = "shopid") String shopid,
                                                    @Param(value = "yearInfo")String yearInfo,
                                                    @Param(value = "monthInfo")String monthInfo,
                                                    @Param(value = "status") Integer status,
                                                    @Param(value = "indexPoint")Integer indexPoint,
                                                    @Param(value = "count")Integer count);

    int updateByPrimaryKeySelective(YdShopActivityInfo record);

    int updateByPrimaryKey(YdShopActivityInfo record);
}