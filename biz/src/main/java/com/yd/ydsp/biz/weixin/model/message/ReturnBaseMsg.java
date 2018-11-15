package com.yd.ydsp.biz.weixin.model.message;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * Created by zengyixun on 17/8/24.
 */
public class ReturnBaseMsg implements Serializable {
    /**
     * 接收方帐号（收到的OpenID）
     */
    @XmlElement(name = "ToUserName")
    private String ToUserName;
    /**
     * 开发者微信号
     */
    @XmlElement(name = "FromUserName")
    private String FromUserName;
    /**
     * 消息创建时间 （整型）
     */
    @XmlElement(name = "CreateTime")
    private Integer CreateTime;
    /**
     * 消息类型 text(文本)image(图片),voice(语音),video(视频),music(音乐消息),news(图文消息)
     */
    @XmlElement(name = "MsgType")
    private String MsgType;

    public ReturnBaseMsg() {
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

}
