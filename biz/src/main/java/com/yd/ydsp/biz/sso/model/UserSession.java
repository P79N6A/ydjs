package com.yd.ydsp.biz.sso.model;

import com.yd.ydsp.common.enums.SourceEnum;

import java.io.Serializable;

/**
 * Created by zengyixun on 17/5/27.
 */
public class UserSession implements Serializable {
    private static final long serialVersionUID = 3000000000000000001L;
    private String yid; //当前的sessionId
    private String openid;
    private String unionid;
    private SourceEnum type;
    private String mobile;
    private String email;
    private String userIp;
    private String appid;//授权公众号及小程序的appid
    private String weixinConfigId;//授权公众号及小程序的weixinConfigId

    public String getYid(){return yid;}
    public void setYid(String yid){ this.yid = yid;}

    public String getOpenid(){ return openid;}
    public void  setOpenid(String openid){ this.openid = openid;}

    public String getUnionid(){ return unionid; }
    public void setUnionid(String unionid){ this.unionid = unionid; }

    public SourceEnum getType(){ return type; }
    public void setType(SourceEnum type){ this.type = type; }

    public String getMobile(){ return mobile; }
    public void setMobile(String mobile){ this.mobile = mobile;}

    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email = email; }

    public String getUserIp(){ return userIp; }
    public void setUserIp(String userIp){ this.userIp = userIp; }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getWeixinConfigId() {
        return weixinConfigId;
    }

    public void setWeixinConfigId(String weixinConfigId) {
        this.weixinConfigId = weixinConfigId;
    }
}
