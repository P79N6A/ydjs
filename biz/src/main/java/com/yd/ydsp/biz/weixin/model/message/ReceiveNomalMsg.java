package com.yd.ydsp.biz.weixin.model.message;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by zengyixun on 17/8/22.
 */

@XmlRootElement(name = "xml")
public class ReceiveNomalMsg implements Serializable {
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
     * 消息类型 text
     */
    @XmlElement(name = "MsgType")
    private String MsgType;
    /**
     * 文本消息内容
     */
    @XmlElement(name = "Content")
    private String Content;
    /**
     * 消息id，64位整型
     */
    @XmlElement(name = "MsgId")
    private Long MsgId;

    public ReceiveNomalMsg() {
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

    public String getContent(){ return Content;}
    public void setContent(String Content){ this.Content = Content;}

    public Long getMsgId(){ return  MsgId;}
    public void setMsgId(Long MsgId) { this.MsgId = MsgId; }
}
