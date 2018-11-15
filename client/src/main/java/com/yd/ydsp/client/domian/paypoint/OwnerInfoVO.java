package com.yd.ydsp.client.domian.paypoint;

import java.io.Serializable;

/**
 * 进行负责人信息修改时传入的内容
 */
public class OwnerInfoVO extends WorkerInfoVO implements Serializable {

    /**
     * 身份证号码
     */
    private String identityNumber;
    /**
     * 身机验证码
     */
    private String checkCode;

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }
}
