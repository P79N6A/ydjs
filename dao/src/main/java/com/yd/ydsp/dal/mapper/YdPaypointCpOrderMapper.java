package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPaypointCpOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YdPaypointCpOrderMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdPaypointCpOrder record);

    int insertSelective(YdPaypointCpOrder record);

    YdPaypointCpOrder selectByPrimaryKey(Long id);

    YdPaypointCpOrder selectByOrderId(String orderid);

    /**
     * (查询页数-1) ＊ 数量 = indexPoint
     * @param openid
     * @param indexPoint
     * @param count
     * @return
     */
    List<YdPaypointCpOrder> selectByOpenid(@Param(value = "openid") String openid,
                                           @Param(value = "indexPoint")Integer indexPoint,
                                           @Param(value = "count")Integer count);

    List<YdPaypointCpOrder> selectByStatus(@Param(value = "status") Integer status,
                                           @Param(value = "indexPoint")Integer indexPoint,
                                            @Param(value = "count")Integer count);

    YdPaypointCpOrder selectByOrderIdRowLock(String orderid);

    int updateByPrimaryKeySelective(YdPaypointCpOrder record);

    int updateByPrimaryKey(YdPaypointCpOrder record);
}