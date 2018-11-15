package com.yd.ydsp.biz.ys7;

import com.yd.ydsp.common.enums.monitor.MonitorAddressTypeEnum;
import com.yd.ydsp.common.enums.monitor.MonitorBaseAddressTypeEnum;

import java.io.IOException;
import java.util.Map;

/**
 * @author zengyixun
 * @date 17/11/27
 */
public interface Ys7Service {
    /**
     * 取萤石的api访问token，缓存没有时会直接调用萤石接口获取并设置缓存
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    String getToken() throws IOException, ClassNotFoundException;

    /**
     * 将缓存中的token删除
     * @throws IOException
     */
    void delToken() throws IOException;

    /**
     * @param deviceSerial
     * @param channelNo
     * @param expireTime
     * @param type
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    String getPlayerAddress(String deviceSerial,Integer channelNo,Integer expireTime, MonitorAddressTypeEnum type) throws IOException, ClassNotFoundException;

    /**
     * 默认取HLS流畅
     * @param deviceSerial
     * @param channelNo
     * @param expireTime
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    String getPlayerAddress(String deviceSerial,Integer channelNo,Integer expireTime) throws IOException, ClassNotFoundException;

    /**
     * 默认取HLS流畅,且只播5分钟
     * @param deviceSerial
     * @param channelNo
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    String getPlayerAddress(String deviceSerial,Integer channelNo) throws IOException, ClassNotFoundException;

    /**
     * 默认取1通道HLS流畅,且只播5分钟
     * @param deviceSerial
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    String getPlayerAddress(String deviceSerial) throws IOException, ClassNotFoundException;


    /**
     * 取原始的唯一播放地址
     * @param deviceSerial
     * @param channelNo
     * @param type
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    String getPlayerOnlyAddress(String deviceSerial,Integer channelNo, MonitorBaseAddressTypeEnum type) throws IOException, ClassNotFoundException;

    /**
     * 取设备通道信息
     * @param deviceSerial
     * @param channelNo
     * @return
        字段名	类型	描述
        deviceSerial	String	设备序列号
        ipcSerial	String	IPC序列号
        channelNo	String	通道号
        deviceName	String	设备名
        channelName	String	通道名
        status	int	在线状态：0-不在线，1-在线,-1设备未上报
        isShared	String	分享状态：1-分享所有者，0-未分享，2-分享接受者（表示此摄像头是别人分享给我的）
        picUrl	String	图片地址（大图），若在萤石客户端设置封面则返回封面图片，未设置则返回默认图片
        isEncrypt	int	是否加密，0：不加密，1：加密
        videoLevel	int	视频质量：0-流畅，1-均衡，2-高清，3-超清
        relatedIpc	boolean	当前通道是否关联IPC：true-是，false-否。设备未上报或者未关联都是false
        注：获取到的通道信息，若NVR设备自动上报关联的IPC信息则返回的是IPC的信息，若NVR设备不进行上报，将获取不到关联的IPC信息。
     */
    Map<String,Object> getChannelInfo(String deviceSerial, Integer channelNo) throws IOException, ClassNotFoundException;

}
