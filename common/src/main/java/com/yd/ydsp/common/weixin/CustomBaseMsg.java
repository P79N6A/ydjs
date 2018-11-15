package com.yd.ydsp.common.weixin;

import java.io.Serializable;

public class CustomBaseMsg implements Serializable {

    public String touser;
    public String msgtype;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }
}
