package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdShopConsumerOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YdShopConsumerOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdShopConsumerOrder record);

    int insertSelective(YdShopConsumerOrder record);

    YdShopConsumerOrder selectByPrimaryKey(Long id);

    List<YdShopConsumerOrder> selectByIds(List<Long> ids);

    List<YdShopConsumerOrder> selectByOpenid(String openid);

    List<YdShopConsumerOrder> selectByOpenidAndStatus(@Param(value = "openid") String openid, @Param(value = "status")Integer status);

    List<YdShopConsumerOrder> selectByOpenidInShop(@Param(value = "openid") String openid, @Param(value = "shopid") String shopid);
    List<YdShopConsumerOrder> selectByOpenidAndStatusInShop(@Param(value = "openid") String openid, @Param(value = "shopid") String shopid, @Param(value = "status") Integer status);

    List<YdShopConsumerOrder> selectByShopid(String shopid);
    List<YdShopConsumerOrder> selectByShopidAndStatus(@Param(value = "shopid") String shopid, @Param(value = "status") Integer status);

    YdShopConsumerOrder selectByOrderid(String orderid);

    YdShopConsumerOrder selectByOrderidRowLock(String orderid);

    List<YdShopConsumerOrder> selectByOrderManager(@Param(value = "firstid") Long firstid,
                                               @Param(value = "lastid") Long lastid,
                                               @Param(value = "shopid") String shopid);

    List<YdShopConsumerOrder> selectByOrderManagerHaveTableId(@Param(value = "firstid") Long firstid,
                                                          @Param(value = "lastid") Long lastid,
                                                          @Param(value = "shopid") String shopid,
                                                          @Param(value = "channelId") String channelId);

    int updateByPrimaryKeySelective(YdShopConsumerOrder record);

    int updateByPrimaryKey(YdShopConsumerOrder record);
}