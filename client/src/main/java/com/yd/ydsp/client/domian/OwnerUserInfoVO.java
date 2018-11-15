package com.yd.ydsp.client.domian;

import java.io.Serializable;

public class OwnerUserInfoVO extends UserInfoVO implements Serializable {

    /**
     * 身份证号码
     */
    private String identityNumber;

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }
}
