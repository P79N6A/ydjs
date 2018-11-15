package com.yd.ydsp.biz.weixin.model.message;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by zengyixun on 17/8/25.
 */

@XmlRootElement(name = "xml")
public class SubscribeEvent extends ReceiveBaseEventMsg {
    /**
     * Subscribe/UnSubscribe一般是空的
     */
    @XmlElement(name = "EventKey")
    private String EventKey;

    public SubscribeEvent() {
        super();
    }

    public String getEventKey(){ return EventKey;}
    public void setEventKey(String EventKey){this.EventKey = EventKey;}

}
