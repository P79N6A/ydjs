package com.yd.ydsp.biz.weixin.model;

import java.io.Serializable;

/**
 * Created by zengyixun on 17/5/16.
 */
public class WeixinTokenInfo implements Serializable {
    private static final long serialVersionUID = 1000000000000000001L;

    private String access_token;
    private String refresh_token;
    private String openid;
    private String unionid;
    private String scope;
    private Integer expires_in;
    private Integer errcode = 0;
    private String errmsg;

    public String getAccess_token(){ return access_token;}
    public String getRefresh_token(){ return refresh_token;}
    public String getOpenid(){ return openid;}

    public String getUnionid() {
        return unionid;
    }

    public String getScope(){ return scope;}
    public Integer getExpires_in(){ return expires_in;}
    public Integer getErrcode(){ return errcode;}
    public String getErrmsg(){ return errmsg;}

    public void setAccess_token(String access_token){ this.access_token = access_token;}
    public void setRefresh_token(String refresh_token){ this.refresh_token = refresh_token;}
    public void setOpenid(String openid){ this.openid = openid;}

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public void setScope(String scope){ this.scope = scope;}
    public void setExpires_in(Integer expires_in){ this.expires_in = expires_in;}
    public void setErrcode(Integer errcode){ this.errcode = errcode;}
    public void setErrmsg(String errmsg){ this.errmsg = errmsg;}

}
