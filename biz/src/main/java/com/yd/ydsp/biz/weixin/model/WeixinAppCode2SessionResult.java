package com.yd.ydsp.biz.weixin.model;

import java.io.Serializable;

/**
 * Created by zengyixun on 17/9/2.
 */
public class WeixinAppCode2SessionResult implements Serializable {
    private static final long serialVersionUID = 1000000000000000003L;

    /**
     * 用户唯一标识
     */
    private String openid;
    /**
     * 会话密钥
     */
    private String session_key;
    /**
     * 如果开发者拥有多个移动应用、网站应用、和公众帐号（包括小程序），可通过unionid来区分用户的唯一性，因为只要是同一个微信开放平台帐号下的移动应用、网站应用和公众帐号（包括小程序），用户的unionid是唯一的。换句话说，同一用户，对同一个微信开放平台下的不同应用，unionid是相同的。

     同一个微信开放平台下的相同主体的App、公众号、小程序，如果用户已经关注公众号，或者曾经登录过App或公众号，则用户打开小程序时，开发者可以直接通过wx.login获取到该用户UnionID，无须用户再次授权。
     */
    private String unionid;

    /**
     * 错误时返回
     */
    private Integer errcode = 0;

    private String errmsg = null;

    public String getOpenid(){
        return openid;
    }
    public void setOpenid(String openid){ this.openid = openid; }

    public String getSession_key(){ return session_key; }

    public void setSession_key(String session_key){ this.session_key = session_key; }

    public String getUnionid(){ return unionid; }
    public void setUnionid(String unionid){ this.unionid = unionid; }

    public String getErrmsg(){ return errmsg;}
    public void setErrmsg(String errmsg){ this.errmsg = errmsg; }

    public Integer getErrcode(){ return errcode; }
    public void setErrcode(Integer errcode){ this.errcode = errcode; }

}
