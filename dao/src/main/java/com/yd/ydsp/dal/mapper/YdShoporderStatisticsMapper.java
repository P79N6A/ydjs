package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdShoporderStatistics;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface YdShoporderStatisticsMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdShoporderStatistics record);

    int insertSelective(YdShoporderStatistics record);

    YdShoporderStatistics selectByPrimaryKey(Long id);

    YdShoporderStatistics selectByShopidRowLock(@Param(value = "shopid") String shopid,@Param(value = "orderDate") Date orderDate);
    YdShoporderStatistics selectByShopid(@Param(value = "shopid") String shopid,@Param(value = "orderDate") Date orderDate);

    List<YdShoporderStatistics> selectByOrderDate(@Param(value = "shopid") String shopid,
                                                  @Param(value = "orderDateBegin") Date orderDateBegin,
                                                  @Param(value = "orderDateEnd") Date orderDateEnd,
                                                  @Param(value = "indexPoint")Integer indexPoint,
                                                  @Param(value = "count")Integer count);

    int updateByPrimaryKeySelective(YdShoporderStatistics record);

    int updateByPrimaryKey(YdShoporderStatistics record);
}