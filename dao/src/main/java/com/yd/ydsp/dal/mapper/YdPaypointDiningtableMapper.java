package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPaypointDiningtable;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YdPaypointDiningtableMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdPaypointDiningtable record);

    int insertSelective(YdPaypointDiningtable record);

    YdPaypointDiningtable selectByPrimaryKey(Long id);

    /**
     * (查询页数-1) ＊ 数量 = indexPoint
     * @param shopid
     * @param indexPoint
     * @param count
     * @return
     */
    List<YdPaypointDiningtable> selectByShopId(@Param(value = "shopid") String shopid,@Param(value = "indexPoint") Integer indexPoint, @Param(value = "count") Integer count);
    YdPaypointDiningtable selectByTableId(String tableid);
    YdPaypointDiningtable selectByQrcode(String qrcode);

    int updateByPrimaryKeySelective(YdPaypointDiningtable record);

    int updateByPrimaryKey(YdPaypointDiningtable record);
}