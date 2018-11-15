package com.yd.ydsp.client.domian;

import java.io.Serializable;
import java.util.Date;

/**
 * 用于返回给前端进行用户信息的处理与判断，必须要此用户验证通过的情况下才返回这些信息哟！！！
 */
public class UserInfoVO implements Serializable {

    /**
     * 昵称
     */
    private String nick;

    /**
     * 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
     */
    private Integer sex;

    private String city;

    private String province;

    private String country;

    /**
     * 头像图片Url
     */
    private String headImgUrl;

    private String mobile;

    private String email;

    private Integer status;

    /**
     * 用户类型或者说用户权限,0为无类型,1为owner，2为manager，3为waiter
     */
    private Integer userType;

    private Date createDate;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick == null ? null : nick.trim();
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country == null ? null : country.trim();
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl == null ? null : headImgUrl.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

}
