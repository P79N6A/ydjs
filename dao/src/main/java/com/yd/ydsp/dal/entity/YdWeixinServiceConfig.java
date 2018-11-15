package com.yd.ydsp.dal.entity;

import java.util.Date;

public class YdWeixinServiceConfig {
    private Long id;

    private String accessToken;

    private String authorizerRefreshToken;

    private Date expiresInDate;

    private String weixinConfigId;

    private Integer weixinType;

    private String appid;

    private String shopid;

    private String secret;

    private String requestUrl;

    private String msgToken;

    private String encodingAesKey;

    private Integer encodeType;

    private String qrcodeUrl;

    private String weixinpaySubMchId;

    private Integer status;

    private String feature;

    private Date createDate;

    private Date modifyDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken == null ? null : accessToken.trim();
    }

    public String getAuthorizerRefreshToken() {
        return authorizerRefreshToken;
    }

    public void setAuthorizerRefreshToken(String authorizerRefreshToken) {
        this.authorizerRefreshToken = authorizerRefreshToken == null ? null : authorizerRefreshToken.trim();
    }

    public Date getExpiresInDate() {
        return expiresInDate;
    }

    public void setExpiresInDate(Date expiresInDate) {
        this.expiresInDate = expiresInDate;
    }

    public String getWeixinConfigId() {
        return weixinConfigId;
    }

    public void setWeixinConfigId(String weixinConfigId) {
        this.weixinConfigId = weixinConfigId == null ? null : weixinConfigId.trim();
    }

    public Integer getWeixinType() {
        return weixinType;
    }

    public void setWeixinType(Integer weixinType) {
        this.weixinType = weixinType;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid == null ? null : appid.trim();
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid == null ? null : shopid.trim();
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret == null ? null : secret.trim();
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl == null ? null : requestUrl.trim();
    }

    public String getMsgToken() {
        return msgToken;
    }

    public void setMsgToken(String msgToken) {
        this.msgToken = msgToken == null ? null : msgToken.trim();
    }

    public String getEncodingAesKey() {
        return encodingAesKey;
    }

    public void setEncodingAesKey(String encodingAesKey) {
        this.encodingAesKey = encodingAesKey == null ? null : encodingAesKey.trim();
    }

    public Integer getEncodeType() {
        return encodeType;
    }

    public void setEncodeType(Integer encodeType) {
        this.encodeType = encodeType;
    }

    public String getQrcodeUrl() {
        return qrcodeUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl == null ? null : qrcodeUrl.trim();
    }

    public String getWeixinpaySubMchId() {
        return weixinpaySubMchId;
    }

    public void setWeixinpaySubMchId(String weixinpaySubMchId) {
        this.weixinpaySubMchId = weixinpaySubMchId == null ? null : weixinpaySubMchId.trim();
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
}