package com.yd.ydsp.biz.weixin.model;

import com.yd.ydsp.common.lang.StringUtil;

import java.io.Serializable;

public class WeixinSmallUserInfo extends WeixinUserInfo implements Serializable {

    private String encryptedData;
    private String iv;
    private String sign;

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        if(StringUtil.isNotEmpty(encryptedData)){
            this.encryptedData = encryptedData.replace(' ','+');
        }else {
            this.encryptedData = encryptedData;
        }
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        if(StringUtil.isNotEmpty(iv)){
            this.iv = iv.replace(' ','+');
        }else {
            this.iv = iv;
        }
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        if(StringUtil.isNotEmpty(sign)){
            this.sign = sign.replace(' ','+');
        }else {
            this.sign = sign;
        }
    }
}
