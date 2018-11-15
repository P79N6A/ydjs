package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdConsumerOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YdConsumerOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdConsumerOrder record);

    int insertSelective(YdConsumerOrder record);

    YdConsumerOrder selectByPrimaryKey(Long id);

    List<YdConsumerOrder> selectByOpenid(@Param(value = "openid") String openid,
                                         @Param(value = "indexPoint")Integer indexPoint,
                                         @Param(value = "count")Integer count);
    List<YdConsumerOrder> selectByOpenidIsPay(@Param(value = "openid") String openid,
                                         @Param(value = "indexPoint")Integer indexPoint,
                                         @Param(value = "count")Integer count);
    List<YdConsumerOrder> selectByOpenidIsNoPay(@Param(value = "openid") String openid,
                                         @Param(value = "indexPoint")Integer indexPoint,
                                         @Param(value = "count")Integer count);
    List<YdConsumerOrder> selectByShopid(String shopid);
    List<YdConsumerOrder> selectByOrderManager(@Param(value = "firstid") Long firstid,
                                               @Param(value = "lastid") Long lastid,
                                         @Param(value = "shopid") String shopid,
                                         @Param(value = "indexPoint")Integer indexPoint,
                                         @Param(value = "count")Integer count);

    List<YdConsumerOrder> selectByOrderManagerHaveTableId(@Param(value = "firstid") Long firstid,
                                               @Param(value = "lastid") Long lastid,
                                               @Param(value = "shopid") String shopid,
                                                          @Param(value = "tableid") String tableid,
                                               @Param(value = "indexPoint")Integer indexPoint,
                                               @Param(value = "count")Integer count);

    YdConsumerOrder selectByOrderid(String orderid);
    YdConsumerOrder selectByOrderidRowLock(String orderid);

    int updateByPrimaryKeySelective(YdConsumerOrder record);

    int updateByPrimaryKey(YdConsumerOrder record);
}