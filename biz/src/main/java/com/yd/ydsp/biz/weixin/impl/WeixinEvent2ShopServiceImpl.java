package com.yd.ydsp.biz.weixin.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.config.DiamondWeiXinInfoConfigHolder;
import com.yd.ydsp.biz.message.MqMessageService;
import com.yd.ydsp.biz.process.JobProcessor;
import com.yd.ydsp.biz.weixin.WeixinEvent2ShopService;
import com.yd.ydsp.biz.weixin.model.message.SubscribeEvent;
import com.yd.ydsp.client.domian.paypoint.WeixinUserInfoDTO;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.constants.paypoint.YdUserSupportFlagConstants;
import com.yd.ydsp.common.enums.MqTagEnum;
import com.yd.ydsp.common.enums.paypoint.WeiXinUserStatusEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.WeiXinMessageUtil;
import com.yd.ydsp.common.weixin.Article;
import com.yd.ydsp.common.weixin.NewsMessage;
import com.yd.ydsp.dal.entity.YdWeixinServiceConfig;
import com.yd.ydsp.dal.entity.YdWeixinUserInfo;
import com.yd.ydsp.dal.mapper.YdWeixinServiceConfigMapper;
import com.yd.ydsp.dal.mapper.YdWeixinUserInfoMapper;
import org.dozer.DozerBeanMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by zengyixun on 17/8/25.
 */
public class WeixinEvent2ShopServiceImpl implements WeixinEvent2ShopService {

    @Resource
    private DozerBeanMapper doMapper;

    @Resource
    private MqMessageService mqMessageService;

    @Resource
    private YdWeixinUserInfoMapper ydWeixinUserInfoMapper;
    @Resource
    private YdWeixinServiceConfigMapper ydWeixinServiceConfigMapper;

    @Override
    public String ReceiveEventHandle(String appid, String msg) throws JAXBException, UnsupportedEncodingException {
        String result = "";
        if(StringUtil.isEmpty(msg)){
            return result;
        }

        if(msg.indexOf("<MsgType><![CDATA[event]]></MsgType>")>0){
            //说明是事件消息
            result = this.EventConsumer(appid,msg);
            return result;
        }else if(msg.indexOf("<MsgType><![CDATA[text]]></MsgType>")>0){
            //说明是普通消息中的文件本消息
            return result;
        }else if(msg.indexOf("<MsgType><![CDATA[image]]></MsgType>")>0){
            //说明是普通消息中的图片消息
            return result;
        }else if(msg.indexOf("<MsgType><![CDATA[voice]]></MsgType>")>0){
            //说明是普通消息中的语音消息
            return result;
        }else if(msg.indexOf("<MsgType><![CDATA[video]]></MsgType>")>0){
            //说明是普通消息中的视频消息
            return result;
        }else if(msg.indexOf("<MsgType><![CDATA[shortvideo]]></MsgType>")>0){
            //说明是普通消息中的小视频消息
            return result;
        }else if(msg.indexOf("<MsgType><![CDATA[location]]></MsgType>")>0){
            //说明是普通消息中的地理位置消息
            return "success";
        }else if(msg.indexOf("<MsgType><![CDATA[link]]></MsgType>")>0){
            //说明是普通消息中的链接消息
            return result;
        }else{
            return result;
        }
    }


    private String EventConsumer(String appid,String msg ) throws JAXBException, UnsupportedEncodingException {
        String result = "";
        if(msg.indexOf("<Event><![CDATA[subscribe]]></Event>")>0){
            //用户未关注时，进行关注后的事件推送
            JAXBContext context = JAXBContext.newInstance(SubscribeEvent.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            SubscribeEvent subscribeEvent = (SubscribeEvent)unmarshaller.unmarshal(new StringReader(msg));
            this.OnSubscribeEvent(appid,subscribeEvent, WeiXinUserStatusEnum.SUBSCRIBE);
            /**
             * 回转关注图文消息
             */
            Article article = new Article();
            List<Article> articleList = new ArrayList<>();
            articleList.add(article);
            NewsMessage subscribeReturn = new NewsMessage();
            subscribeReturn.setArticleCount(1);
            subscribeReturn.setMsgType(WeiXinMessageUtil.RESP_MESSAGE_TYPE_NEWS);
            subscribeReturn.setArticles(articleList);
            article.setTitle(new String(DiamondWeiXinInfoConfigHolder.getInstance().getB2bSubscribeTitle().getBytes(),"iso8859-1"));
            article.setPicUrl(DiamondWeiXinInfoConfigHolder.getInstance().getB2bSubscribePicUrl());
            article.setDescription(new String(DiamondWeiXinInfoConfigHolder.getInstance().getB2bSubscribeDesc().getBytes(), "iso8859-1"));
            article.setUrl(DiamondWeiXinInfoConfigHolder.getInstance().getB2bSubscribePageUrl());
            subscribeReturn.setMsgType(WeiXinMessageUtil.RESP_MESSAGE_TYPE_NEWS);
            subscribeReturn.setCreateTime(new Long(new Date().getTime()).intValue());
            subscribeReturn.setToUserName(subscribeEvent.getFromUserName());
            subscribeReturn.setFromUserName(subscribeEvent.getToUserName());
            return WeiXinMessageUtil.getInstance().newsMessageToXml(subscribeReturn);
        }else if(msg.indexOf("<Event><![CDATA[unsubscribe]]></Event>")>0){
            //用户取消关注
            JAXBContext context = JAXBContext.newInstance(SubscribeEvent.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            SubscribeEvent subscribeEvent = (SubscribeEvent)unmarshaller.unmarshal(new StringReader(msg));
            this.OnSubscribeEvent(appid,subscribeEvent, WeiXinUserStatusEnum.UNSUBSCRIBE);
            return "success";
        }else if(msg.indexOf("<Event><![CDATA[SCAN]]></Event>")>0){
            //用户已关注时的事件推送
            //不做任何事
            return result;
        }else if(msg.indexOf("<Event><![CDATA[LOCATION]]></Event>")>0){
            //上报地理位置事件
            return result;
        }else if(msg.indexOf("<Event><![CDATA[CLICK]]></Event>")>0){
            //点击菜单拉取消息时的事件推送
            return result;
        }else if(msg.indexOf("<Event><![CDATA[VIEW]]></Event>")>0){
            //点击菜单跳转链接时的事件推送
            return result;
        }
        return result;

    }

    @Transactional(rollbackFor = Exception.class)
    protected void OnSubscribeEvent(String appid, SubscribeEvent se,WeiXinUserStatusEnum statusEnum){
        if(statusEnum== WeiXinUserStatusEnum.SUBSCRIBE) {

            YdWeixinServiceConfig weixinServiceConfig = ydWeixinServiceConfigMapper.selectByAppid(appid);
            if(weixinServiceConfig==null){
                return;
            }
            YdWeixinUserInfo weixinUserInfo = ydWeixinUserInfoMapper.selectByOpenidLockRow(weixinServiceConfig.getWeixinConfigId(),se.getFromUserName());
            if (weixinUserInfo == null) {
                //开始入库
                WeixinUserInfoDTO weixinUserInfoDTO = new WeixinUserInfoDTO();
                weixinUserInfoDTO.setOpenid(se.getFromUserName());
                weixinUserInfoDTO.setModifier("system");
                weixinUserInfoDTO.addFlag(YdUserSupportFlagConstants.NEEDUPDATE);
                weixinUserInfoDTO.setStatus(statusEnum.getType());
                weixinUserInfo = doMapper.map(weixinUserInfoDTO, YdWeixinUserInfo.class);
                ydWeixinUserInfoMapper.insert(weixinUserInfo);
            } else {
                weixinUserInfo.setStatus(statusEnum.getType());
                ydWeixinUserInfoMapper.updateByPrimaryKey(weixinUserInfo);
            }


            /**
             * 调用指定公众号或者小程序的用户信息
             */
            Map<String,Object> in = new HashMap<>();
            in.put(Constant.MQTAG, MqTagEnum.WEIXINUSERMSG.getTag());
            in.put("weixinType", weixinServiceConfig.getWeixinType());
            in.put("appid", appid);
            in.put("openid",se.getFromUserName());
            in.put("isSns",false);
            //使用消息机制来处理
            mqMessageService.sendMessage(appid+se.getFromUserName(),MqTagEnum.WEIXINUSERMSG, JSON.toJSONString(in));

        }else if(statusEnum== WeiXinUserStatusEnum.UNSUBSCRIBE){

            YdWeixinUserInfo weixinUserInfo = ydWeixinUserInfoMapper.selectByOpenidLockRow(appid,se.getFromUserName());
            weixinUserInfo.setStatus(statusEnum.getType());
            ydWeixinUserInfoMapper.updateByPrimaryKey(weixinUserInfo);

        }
    }

}
