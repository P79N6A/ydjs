package com.yd.ydsp.biz.cp;

import com.yd.ydsp.biz.cp.model.CPMonitorVO;

import java.io.IOException;
import java.util.Map;

/**
 * @author zengyixun
 * @date 17/12/17
 */
public interface CpMonitorService {
    /**
     * 取一个店铺监控点的信息
     * @param shopid
     * @return
     */
    CPMonitorVO getShopMonitorInfo(String shopid);

    /**
     * 设置店铺监控点的信息
     * @param monitorVO
     * @return
     */
    Boolean setShopMonitorInfo(CPMonitorVO monitorVO);

    /**
     * 直接取到设备单通道上的播放地址
     * @param deviceSerial
     * @param channelNo
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    String getPlayAddress(String deviceSerial,Integer channelNo) throws IOException, ClassNotFoundException;


    /**
     * 直接取到店铺单通道上的播放地址
     * @param deviceSerial
     * @param channelNo
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    Map<String,Object> getChannelInfo(String deviceSerial, Integer channelNo) throws IOException, ClassNotFoundException;

    /**
     * 获取上传文件的token
     */
    Map<String,Object> getOssAuthentication(String mobile,String code,String key) throws IOException, ClassNotFoundException;
}
