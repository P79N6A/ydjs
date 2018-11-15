package com.yd.ydsp.common.model;

import com.yd.ydsp.common.constants.ErrorCodeConstants;

import java.io.Serializable;

/**
 * Created by zengyixun on 17/1/19.
 */
public class  Result<T> implements Serializable {
    private static final long serialVersionUID = -870716084359345454L;

    private boolean success;
    private T result;
    private String resultCode;
    private String msgInfo;

    public static <T> Result<T> successResult(T t) {
        Result<T> result = new Result<T>();
        result.setSuccess(true);
        result.setResultCode(ErrorCodeConstants.SUCCESS.getErrorCode());
        result.setResult(t);
        return result;
    }

    public static <T> Result<T> successResultWithMsg(T t, String msg) {
        Result<T> result = new Result<T>();
        result.setSuccess(true);
        result.setResultCode(ErrorCodeConstants.SUCCESS.getErrorCode());
        result.setMsgInfo(msg);
        result .setResult(t);
        return result;
    }

    public static <T> Result<T> failResult(T t, String failCode) {
        Result<T> result = new Result<T>();
        result.setSuccess(false);
        result.setResult(t);
        result.setResultCode(failCode);
        return result;
    }

    public static <T> Result<T> failResult(String failCode) {
        return failResult(null, failCode);
    }

    public static <T> Result<T> failResultWithMsg(T t, String msgInfo) {
        Result<T> result = new Result<T>();
        result.setSuccess(false);
        result.setResult(t);
        result.setMsgInfo(msgInfo);
        return result;
    }

    public static <T> Result<T> failResultWithMsg(String msgInfo) {
        return failResultWithMsg(null, msgInfo);
    }

    @Override
    public String toString() {
        return isSuccess() + "[ResultCode : " + getResultCode() + "] [result : " + getResult() + "]";
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getMsgInfo() {
        return msgInfo;
    }

    public void setMsgInfo(String msgInfo) {
        this.msgInfo = msgInfo;
    }
}
