package com.yd.ydsp.common.model;

/**
 * Created by zengyixun on 17/1/19.
 */
public enum ResultCode {
    SUCCESS(0, "操作成功"),
    FAIL(-1, "操作失败，请重试"),
    PARAM_ERROR(-2, "参数错误，请检查"),
    PASSPORT_ERROR(-3,"没有操作权限，请检查"),
    WEIXIN_CODE_ERROR(-4,"微信code无效");
    private int errorCode;
    private String msg;

    ResultCode(int errorCode, String msg) {
        this.errorCode = errorCode;
        this.msg = msg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
