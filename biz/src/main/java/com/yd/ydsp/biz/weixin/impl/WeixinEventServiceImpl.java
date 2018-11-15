package com.yd.ydsp.biz.weixin.impl;

import com.yd.ydsp.biz.config.DiamondWeiXinInfoConfigHolder;
import com.yd.ydsp.biz.process.JobProcessor;
import com.yd.ydsp.biz.weixin.WeixinEventService;
import com.yd.ydsp.biz.weixin.model.message.SubscribeEvent;
import com.yd.ydsp.client.domian.paypoint.ConsumerInfoDTO;
import com.yd.ydsp.client.domian.paypoint.CpUserInfoDTO;
import com.yd.ydsp.common.constants.paypoint.YdUserSupportFlagConstants;
import com.yd.ydsp.common.enums.SourceEnum;
import com.yd.ydsp.common.enums.paypoint.WeiXinUserStatusEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.DateUtils;
import com.yd.ydsp.common.utils.WeiXinMessageUtil;
import com.yd.ydsp.common.weixin.Article;
import com.yd.ydsp.common.weixin.NewsMessage;
import com.yd.ydsp.dal.entity.YdConsumerInfo;
import com.yd.ydsp.dal.entity.YdPaypointCpuserInfo;
import com.yd.ydsp.dal.mapper.YdConsumerInfoMapper;
import com.yd.ydsp.dal.mapper.YdPaypointCpuserInfoMapper;
import org.dom4j.DocumentException;
import org.dozer.DozerBeanMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by zengyixun on 17/8/25.
 */
public class WeixinEventServiceImpl implements WeixinEventService {

    private String type;
    private SourceEnum weixinType;
    private String msgToken;

    @Resource
    private DozerBeanMapper doMapper;

    @Resource
    private JobProcessor getWeinXinUserInfoProcess;

    @Resource
    private YdPaypointCpuserInfoMapper ydPaypointCpuserInfoMapper;

    @Resource
    private YdConsumerInfoMapper ydConsumerInfoMapper;

    private NewsMessage subscribeReturn;
    private Article article;

    public void setType(String type){ this.type = type.trim();  this.weixinType = SourceEnum.valueOf(this.type);}

    public String getMsgToken(){ return msgToken; }
    public void setMsgToken(String msgToken){ this.msgToken = msgToken;}


    public void init(){

        if(weixinType==SourceEnum.WEIXIN2B){
            article = new Article();
            List<Article> articleList = new ArrayList<>();
            articleList.add(article);
            subscribeReturn = new NewsMessage();
            subscribeReturn.setArticleCount(1);
            subscribeReturn.setMsgType(WeiXinMessageUtil.RESP_MESSAGE_TYPE_NEWS);
            subscribeReturn.setArticles(articleList);
        }

    }

    @Override
    public String ReceiveEventHandle(String msg) throws JAXBException, UnsupportedEncodingException {
        String result = "";
        if(StringUtil.isEmpty(msg)){
            return result;
        }

        if(msg.indexOf("<MsgType><![CDATA[event]]></MsgType>")>0){
            //说明是事件消息
            result = this.EventConsumer(msg);
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

    @Override
    public Boolean checkToken(String token) {
        return this.msgToken.equals(token);
    }


    private String EventConsumer(String msg ) throws JAXBException, UnsupportedEncodingException {
        String result = "";
        if(msg.indexOf("<Event><![CDATA[subscribe]]></Event>")>0){
            //用户未关注时，进行关注后的事件推送
            JAXBContext context = JAXBContext.newInstance(SubscribeEvent.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            SubscribeEvent subscribeEvent = (SubscribeEvent)unmarshaller.unmarshal(new StringReader(msg));
            this.OnSubscribeEvent(subscribeEvent, WeiXinUserStatusEnum.SUBSCRIBE);
            /**
             * 回转关注图文消息
             */
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
            JAXBContext context = JAXBContext.newInstance(SubscribeEvent.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            SubscribeEvent subscribeEvent = (SubscribeEvent)unmarshaller.unmarshal(new StringReader(msg));
            this.OnSubscribeEvent(subscribeEvent, WeiXinUserStatusEnum.UNSUBSCRIBE);
            return result;
            //用户取消关注
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
    protected void OnSubscribeEvent(SubscribeEvent se,WeiXinUserStatusEnum statusEnum){
        if(statusEnum== WeiXinUserStatusEnum.SUBSCRIBE) {
            if(weixinType==SourceEnum.WEIXIN2B) {
                YdPaypointCpuserInfo paypointCpuserInfo = ydPaypointCpuserInfoMapper.selectByOpenidLockRow(se.getFromUserName());
                if (paypointCpuserInfo == null) {
                    //开始入库
                    CpUserInfoDTO cpUserInfoDTO = new CpUserInfoDTO();
                    cpUserInfoDTO.setOpenid(se.getFromUserName());
                    cpUserInfoDTO.setModifier("system");
                    cpUserInfoDTO.addFlag(YdUserSupportFlagConstants.NEEDUPDATE);
                    cpUserInfoDTO.setStatus(statusEnum.getType());
                    paypointCpuserInfo = doMapper.map(cpUserInfoDTO, YdPaypointCpuserInfo.class);
                    ydPaypointCpuserInfoMapper.insert(paypointCpuserInfo);
                } else {
                    paypointCpuserInfo.setStatus(statusEnum.getType());
                    ydPaypointCpuserInfoMapper.updateByPrimaryKey(paypointCpuserInfo);
                }
            }

            if(weixinType==SourceEnum.WEIXIN2C) {
                YdConsumerInfo consumerInfo = ydConsumerInfoMapper.selectByOpenid(se.getFromUserName());
                if(consumerInfo == null){
                    //开始入库Ｃ端
                    ConsumerInfoDTO consumerInfoDTO = new ConsumerInfoDTO();
                    consumerInfoDTO.setOpenid(se.getFromUserName());
                    consumerInfoDTO.setModifier("system");
                    consumerInfoDTO.addFlag(YdUserSupportFlagConstants.NEEDUPDATE);
                    consumerInfoDTO.setStatus(statusEnum.getType());
                    consumerInfo = doMapper.map(consumerInfoDTO,YdConsumerInfo.class);
                    ydConsumerInfoMapper.insert(consumerInfo);
                }else {
                    consumerInfo.setStatus(statusEnum.getType());
                    ydConsumerInfoMapper.updateByPrimaryKey(consumerInfo);
                }
            }

            Map<String,Object> in = new HashMap<>();
            in.put("weixinType", this.type);
            in.put("openid",se.getFromUserName());
            in.put("isSns",false);
            getWeinXinUserInfoProcess.process(in);

        }else if(statusEnum== WeiXinUserStatusEnum.UNSUBSCRIBE){
            if(weixinType==SourceEnum.WEIXIN2B) {
                YdPaypointCpuserInfo paypointCpuserInfo = ydPaypointCpuserInfoMapper.selectByOpenidLockRow(se.getFromUserName());
                paypointCpuserInfo.setStatus(statusEnum.getType());
                ydPaypointCpuserInfoMapper.updateByPrimaryKey(paypointCpuserInfo);
            }
            if(weixinType==SourceEnum.WEIXIN2C){
                YdConsumerInfo consumerInfo = ydConsumerInfoMapper.selectByOpenid(se.getFromUserName());
                consumerInfo.setStatus(statusEnum.getType());
                ydConsumerInfoMapper.updateByPrimaryKey(consumerInfo);
            }
        }
    }

}
