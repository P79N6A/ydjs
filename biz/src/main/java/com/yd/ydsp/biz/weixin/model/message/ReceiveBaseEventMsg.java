package com.yd.ydsp.biz.weixin.model.message;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * Created by zengyixun on 17/8/22.
 */

public class ReceiveBaseEventMsg implements Serializable {
    /**
     * 开发者微信号
     */
    @XmlElement(name = "ToUserName")
    private String ToUserName;
    /**
     * 发送方帐号（一个OpenID）
     */
    @XmlElement(name = "FromUserName")
    private String FromUserName;
    /**
     * 消息创建时间 （整型）
     */
    @XmlElement(name = "CreateTime")
    private Integer CreateTime;
    /**
     * 消息类型 event
     */
    @XmlElement(name = "MsgType")
    private String MsgType;
    /**
     * 事件类型，subscribe(订阅)、unsubscribe(取消订阅)
     */
    @XmlElement(name = "Event")
    private String Event;

    public ReceiveBaseEventMsg() {
        super();
    }

    public String getToUserName(){ return ToUserName;}
    public void setToUserName(String ToUserName){ this.ToUserName = ToUserName;}

    public String getFromUserName(){ return FromUserName; }
    public void setFromUserName(String FromUserName){ this.FromUserName = FromUserName;}

    public Integer getCreateTime(){ return CreateTime; }
    public void setCreateTime(Integer CreateTime){ this.CreateTime = CreateTime;}

    public String getMsgType(){ return  MsgType;}
    public void setMsgType(String MsgType){ this.MsgType = MsgType;}

    public String getEvent(){ return Event;}
    public void setEvent(String Event){ this.Event = Event;}
}
