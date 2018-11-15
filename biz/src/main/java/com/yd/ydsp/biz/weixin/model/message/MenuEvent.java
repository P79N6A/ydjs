package com.yd.ydsp.biz.weixin.model.message;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by zengyixun on 17/8/25.
 */

@XmlRootElement(name = "xml")
public class MenuEvent extends ReceiveBaseEventMsg {
    //Event类型，CLICK - 点击菜单拉取消息时的事件推送
    //Event类型，VIEW - 点击菜单跳转链接时的事件推送
    /**
     * 事件KEY值，与自定义菜单接口中KEY值对应
     */
    @XmlElement(name = "EventKey")
    private String EventKey;

    public MenuEvent() {
        super();
    }

    public String getEventKey(){ return EventKey;}
    public void setEventKey(String EventKey){this.EventKey = EventKey;}

}
