package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdTableQrcode;

import java.util.List;

public interface YdTableQrcodeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdTableQrcode record);

    int insertSelective(YdTableQrcode record);

    YdTableQrcode selectByPrimaryKey(Long id);
    YdTableQrcode selectByQrcode(String qrcode);
    List<YdTableQrcode> selectByTable(String tableid);

    int updateByPrimaryKeySelective(YdTableQrcode record);

    int updateByPrimaryKey(YdTableQrcode record);
}