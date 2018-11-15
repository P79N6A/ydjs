package com.yd.ydsp.client.domian.weixin;

import com.yd.ydsp.common.enums.SourceEnum;

import java.io.Serializable;

public class YdWeiXinInfoBase implements Serializable {
    private static final long serialVersionUID = 983188846771659188L;

    /**公众号appid
     *
     */
    private String appid;
    /**
     * 公从号secret
     */
    private String secret;

    private SourceEnum weixinType;
    /**
     * 接收微信消息时，我们在微信设置回调api的token
     */
    private String msgToken;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public SourceEnum getWeixinType() {
        return weixinType;
    }

    public void setWeixinType(SourceEnum weixinType) {
        this.weixinType = weixinType;
    }

    public String getMsgToken() {
        return msgToken;
    }

    public void setMsgToken(String msgToken) {
        this.msgToken = msgToken;
    }
}
