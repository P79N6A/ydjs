package com.yd.ydsp.biz.weixin.model.message;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by zengyixun on 17/8/25.
 */

@XmlRootElement(name = "xml")
public class ScanEvent extends ReceiveBaseEventMsg {
    /**
     * 扫描带参数二维码事件：事件KEY值，qrscene_为前缀，后面为二维码的参数值
     */
    @XmlElement(name = "EventKey")
    private String EventKey;
    /**
     * 扫描带参数二维码事件:二维码的ticket，可用来换取二维码图片
     */
    @XmlElement(name = "Ticket")
    private String Ticket;

    public ScanEvent() {
        super();
    }

    public String getEventKey(){ return EventKey;}
    public void setEventKey(String EventKey){this.EventKey = EventKey;}

    public String getTicket(){ return Ticket;}
    public void setTicket(String Ticket){ this.Ticket = Ticket;}
}
