package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPaypointWaresSku;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YdPaypointWaresSkuMapper {
    int deleteByPrimaryKey(Long id);
    int deleteByPrimaryKeyLogic(Long id);

    int deleteByShopidAndItem(@Param(value = "shopid") String shopid,@Param(value = "wareitemid")String wareitemid);
    int deleteByShopidAndItemLogic(@Param(value = "shopid") String shopid,@Param(value = "wareitemid")String wareitemid);

    int insert(YdPaypointWaresSku record);

    int insertSelective(YdPaypointWaresSku record);

    YdPaypointWaresSku selectByPrimaryKey(Long id);
    YdPaypointWaresSku selectBySkuId(String skuid);
    YdPaypointWaresSku selectBySkuIdRowLock(String skuid);

    List<YdPaypointWaresSku> selectByShopidAndItem(@Param(value = "shopid") String shopid,@Param(value = "wareitemid")String wareitemid);
    List<YdPaypointWaresSku> selectByShopidAndItem2C(@Param(value = "shopid") String shopid,@Param(value = "wareitemid")String wareitemid);

    int updateByPrimaryKeySelective(YdPaypointWaresSku record);

    int updateByPrimaryKey(YdPaypointWaresSku record);
}