package com.yd.ydsp.biz.weixin.model.message;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by zengyixun on 17/8/24.
 */

@XmlRootElement(name = "xml")
public class ReturnContentMsg extends ReturnBaseMsg {
    /**
     * 回复的消息内容（换行：在content中能够换行，微信客户端就支持换行显示）
     */
    @XmlElement(name = "Content")
    private String Content;

    public ReturnContentMsg() {
        super();
    }

    public String getContent(){ return Content;}
    public void setContent(String Content){ this.Content = Content;}

}
