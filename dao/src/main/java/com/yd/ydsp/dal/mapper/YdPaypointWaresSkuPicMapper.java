package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdPaypointWaresSkuPic;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YdPaypointWaresSkuPicMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdPaypointWaresSkuPic record);

    int insertSelective(YdPaypointWaresSkuPic record);

    YdPaypointWaresSkuPic selectByPrimaryKey(Long id);

    /**
     * 商品图文详情
     * @param skuid
     * @return
     */
    List<YdPaypointWaresSkuPic> selectPicDetailBySkuid(String skuid);
    List<YdPaypointWaresSkuPic> selectBySkuidAndPicType(@Param(value = "skuid") String skuid,@Param(value = "picType") Integer picType);

    int updateByPrimaryKeySelective(YdPaypointWaresSkuPic record);

    int updateByPrimaryKey(YdPaypointWaresSkuPic record);
}