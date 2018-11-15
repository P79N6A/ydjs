package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdShopPageInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YdShopPageInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdShopPageInfo record);

    int insertSelective(YdShopPageInfo record);

    YdShopPageInfo selectByPrimaryKey(Long id);
    YdShopPageInfo selectByPrimaryKeyRelease(Long id);
    YdShopPageInfo selectByPageId(String pageid);
    YdShopPageInfo selectByPageIdRelease(String pageid);
    YdShopPageInfo selectByPageIdRowLock(String pageid);
    YdShopPageInfo selectByPageIdReleaseRowLock(String pageid);

    YdShopPageInfo selectByShopIdAndName(@Param(value = "shopid") String shopid,@Param(value = "pageName")String pageName);

    List<YdShopPageInfo> selectByShopId(@Param(value = "shopid") String shopid,
                                        @Param(value = "indexPoint")Integer indexPoint,
                                        @Param(value = "count")Integer count);
    List<YdShopPageInfo> selectByShopIdAndStatus(@Param(value = "shopid") String shopid,@Param(value = "status") Integer status,
                                        @Param(value = "indexPoint")Integer indexPoint,
                                        @Param(value = "count")Integer count);


    int updateByPrimaryKeySelective(YdShopPageInfo record);
    int updateByPrimaryKeySelectiveRelease(YdShopPageInfo record);

    int updateByPrimaryKey(YdShopPageInfo record);
}