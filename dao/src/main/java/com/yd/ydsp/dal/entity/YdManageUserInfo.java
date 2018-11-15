package com.yd.ydsp.dal.entity;

import java.util.Date;

public class YdManageUserInfo {
    private Long id;

    private String unionid;

    private String openid;

    private String nick;

    private Integer sex;

    private String city;

    private String province;

    private String country;

    private String headImgUrl;

    private Integer headImgType;

    private Integer wexGroupId;

    private String mobile;

    private String email;

    private String password;

    private String original;

    private Long flag;

    private Integer status;

    private String feature;

    private Date createDate;

    private Date modifyDate;

    private String modifier;

    private String weixinAccessToken;

    private String weixinRefreshToken;

    private Date weixinTokenExpireIn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid == null ? null : unionid.trim();
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid == null ? null : openid.trim();
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick == null ? null : nick.trim();
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

    public Integer getHeadImgType() {
        return headImgType;
    }

    public void setHeadImgType(Integer headImgType) {
        this.headImgType = headImgType;
    }

    public Integer getWexGroupId() {
        return wexGroupId;
    }

    public void setWexGroupId(Integer wexGroupId) {
        this.wexGroupId = wexGroupId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original == null ? null : original.trim();
    }

    public Long getFlag() {
        return flag;
    }

    public void setFlag(Long flag) {
        this.flag = flag;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier == null ? null : modifier.trim();
    }

    public String getWeixinAccessToken() {
        return weixinAccessToken;
    }

    public void setWeixinAccessToken(String weixinAccessToken) {
        this.weixinAccessToken = weixinAccessToken == null ? null : weixinAccessToken.trim();
    }

    public String getWeixinRefreshToken() {
        return weixinRefreshToken;
    }

    public void setWeixinRefreshToken(String weixinRefreshToken) {
        this.weixinRefreshToken = weixinRefreshToken == null ? null : weixinRefreshToken.trim();
    }

    public Date getWeixinTokenExpireIn() {
        return weixinTokenExpireIn;
    }

    public void setWeixinTokenExpireIn(Date weixinTokenExpireIn) {
        this.weixinTokenExpireIn = weixinTokenExpireIn;
    }
}