package com.yd.ydsp.biz.cp;

import com.yd.ydsp.biz.cp.model.YdDeliveryInfoVO;
import com.yd.ydsp.client.domian.openshop.YdCpChannelVO;
import com.yd.ydsp.common.enums.SourceEnum;

import java.util.List;

public interface CpChannelService {

    /**
     * 取物流配置信息列表
     * @param openid
     * @param shopid
     * @return
     */
    List<YdDeliveryInfoVO> getDeliveryConfigInfo(String openid,String shopid);

    /**
     * 新增一个渠道
     * @param openid
     * @param cpChannelVO
     * @return
     */
    String addChannel(String openid, YdCpChannelVO cpChannelVO) throws Exception;

    /**
     * 修改渠道信息
     * @param openid
     * @param cpChannelVO
     * @return
     */
    boolean modifyChannel(String openid, YdCpChannelVO cpChannelVO);

    /**
     * 停止使用此渠道
     * @param openid
     * @param channelid
     * @return
     */
    boolean disableChannel(String openid,String channelid);

//    /**
//     * 重新启用渠道
//     * @param openid
//     * @param channelid
//     * @return
//     */
//    boolean enableChannel(String openid,String channelid);

    /**
     *
     * @param openid
     * @param shopid
     * @return
     */
    List<YdCpChannelVO> queryChannelList(String openid, String shopid);
    List<YdCpChannelVO> queryChannelList(String openid, String shopid, SourceEnum sourceEnum);

    /**
     * 查询渠道信息
     * @param openid
     * @param channelid
     * @return
     */
    YdCpChannelVO queryChannelInfo(String openid,String channelid);
    YdCpChannelVO queryChannelInfo(String channelid);


}
