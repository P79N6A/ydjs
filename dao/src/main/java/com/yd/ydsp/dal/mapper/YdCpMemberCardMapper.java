package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdCpMemberCard;

import java.util.List;

public interface YdCpMemberCardMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdCpMemberCard record);

    int insertSelective(YdCpMemberCard record);

    YdCpMemberCard selectByPrimaryKey(Long id);
    YdCpMemberCard selectByCradid(String cardid);
    List<YdCpMemberCard> selectByShopid(String shopid);

    int updateByPrimaryKeySelective(YdCpMemberCard record);

    int updateByPrimaryKey(YdCpMemberCard record);
}