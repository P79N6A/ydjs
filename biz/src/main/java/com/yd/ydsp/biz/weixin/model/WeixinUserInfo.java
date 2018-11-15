package com.yd.ydsp.biz.weixin.model;

import java.io.Serializable;

/**
 * Created by zengyixun on 17/5/16.
 */
public class WeixinUserInfo implements Serializable {
    private static final long serialVersionUID = 1000000000000000002L;

    private String openid;//用户的唯一标识
    private String unionid;//只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段
    private String nickname;
    private String mobile;
    private String province;//用户个人资料填写的省份
    private String city;//普通用户个人资料填写的城市
    private String country;//国家，如中国为CN
    private Integer sex;//用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
    /*
    用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
     */
    private String headimgurl;
    private Integer errcode = 0;
    private String errmsg;

    public String getOpenid(){ return openid;}
    public String getUnionid(){ return unionid;}
    public String getNickname(){ return nickname;}
    public String getProvince(){ return province;}
    public String getCity(){ return city;}
    public String getCountry(){ return country;}
    public Integer getSex(){ return sex;}
    public String getHeadimgurl(){ return headimgurl;}
    public Integer getErrcode(){ return errcode;}
    public String getErrmsg(){ return errmsg;}

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public void setOpenid(String openid){ this.openid = openid;}
    public void setUnionid(String unionid){ this.unionid = unionid;}
    public void setNickname(String nickname){ this.nickname = nickname;}
    public void setProvince(String province){ this.province = province;}
    public void setCity(String city){ this.city = city;}
    public void setCountry(String country){ this.country = country;}
    public void setSex(Integer sex){ this.sex = sex;}
    public void setHeadimgurl(String headimgurl){ this.headimgurl = headimgurl;}
    public void setErrcode(Integer errcode){ this.errcode = errcode;}
    public void setErrmsg(String errmsg){ this.errmsg = errmsg;}

}
