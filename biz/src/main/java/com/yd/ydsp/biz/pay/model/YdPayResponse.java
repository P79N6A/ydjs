package com.yd.ydsp.biz.pay.model;

import java.io.Serializable;

/**
 * @author zengyixun
 * @date 17/12/21
 */
public class YdPayResponse implements Serializable {
    boolean success = false;
    String errorCode;
    String errorMsg;
    String payurl;
    String imghexstr;
    String orderid;
    Long payorderid;

    public boolean getSuccess(){ return success; }
    public void setSuccess(boolean success){ this.success = success; }

    public String getErrorCode(){ return errorCode; }
    public void setErrorCode(String errorCode){ this.errorCode = errorCode; }

    public String getErrorMsg(){ return errorMsg; }
    public void setErrorMsg(String errorMsg){ this.errorMsg = errorMsg; }

    public String getPayurl(){ return payurl; }
    public void setPayurl(String payurl){ this.payurl = payurl; }

    public String getImghexstr(){ return imghexstr; }
    public void setImghexstr(String imghexstr){ this.imghexstr = imghexstr; }

    public String getOrderid(){ return orderid; }
    public void setOrderid(String orderid){ this.orderid = orderid; }

    public Long getPayorderid(){ return payorderid; }
    public void setPayorderid(Long payorderid){ this.payorderid = payorderid; }
}
