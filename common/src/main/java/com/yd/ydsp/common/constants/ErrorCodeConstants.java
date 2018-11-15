package com.yd.ydsp.common.constants;

/**
 * Created by zengyixun on 17/1/20.
 */
public enum ErrorCodeConstants {

    SUCCESS("0","成功！"),
    FAIL("-1", "操作失败，请重试"),
    PARAM_ERROR("-2", "参数错误，请检查"),
    PASSPORT_ERROR("-3","没有操作权限，请检查"),
    WEIXIN_CODE_ERROR("-4","微信code无效"),
    WEIXIN_NEED_LOGIN("-5","需要微信登录授权"),
    NEED_LOGIN("-5","需要登录"),
    SHOPISSLEEP("-6","店铺已经休息，请改天再来"),
    /**
     * ========================
     * 通用异常 by 燃灯
     * ========================
     */
    YDSPEXCEPTION_DAO("YDSP_DAO_EXCEPTION", "数据库异常"),
    YDSPEXCEPTION_WRAPPER_ERROR("YDSPEXCEPTION_WRAPPER_ERROR", "外部服务异常"),
    YDSPEXCEPTION_INPUT_ERROR("YDSPEXCEPTION_INPUT_ERROR", "入参错误"),
    YDSPEXCEPTION_BIZ_ERROR("YDSPEXCEPTION_BIZ_ERROR", "业务异常"),
    YDSPEXCEPTION_UNKOWN("YDSPEXCEPTION_SERVER_UNKOWN", "未知异常"),

    YDSPEXCEPTION_RECORD_IS_NULL("YDSPEXCEPTION_RECORD_IS_NULL", "记录没找到"),
    YDSPEXCEPTION_YDSPTUS_CHANGE_NOT_ALLOWED("YDSPEXCEPTION_YDSPTUS_CHANGE_NOT_ALLOWED", "状态变更不允许"),

    YD10000("YD10000", "数据库异常"),
    YD10001("YD10001", "系统异常"),
    YD10002("YD10002", "参数缺失"),
    YD10003("YD10003", "参数错误"),
    YD10004("YD10004", "物流详情处理异常"),
    YD10005("YD10005", "短信处理异常"),
    YD10006("YD10006", "短信码验证失败"),
    YD10007("YD10007", "短信发送太频繁，请稍后再试！"),
    YD10008("YD10008", "用户不存在，你是机器人吗?"),
    //Diamond异常
    YD00901("YD00901", "Diamond通用开关异常"),
    YD00902("YD00902", "Diamond通用开关处理配置信息格式错误"),
    YD00903("YD00903", "Diamond开关工具异常"),
    YD00904("YD00904", "Diamond开关工具处理配置信息格式错误"),
    YD00905("YD00905", "数据转换错误，请检查你的数据正确性"),

    /**
     * 管理后台登录错误码设置
     */
    L100001("L100001", "数据库密码为空!"),
    L100002("L100002", "密码校验错误!"),

    ;



    /** 错误code */
    private String errorCode;

    /** 错误信息 */
    private String errorMessage;

    ErrorCodeConstants(String errorCode, String errorMessage) {

        this.errorCode = errorCode;
        this.errorMessage = errorMessage;

    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorMessage(Object ... args) {
        return String.format(errorMessage, args);
    }

    @Override
    public String toString() {
        return errorCode + "," + errorMessage;
    }
    
}
