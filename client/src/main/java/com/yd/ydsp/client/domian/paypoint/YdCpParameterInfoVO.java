package com.yd.ydsp.client.domian.paypoint;

import com.yd.ydsp.common.lang.StringUtil;

public class YdCpParameterInfoVO {

    /**
     * 商品skuid
     */
    private String skuid;

    /**
     * 参数id
     */
    private String parameterid;

    /**
     * 参数名称
     */
    private String parameterKey;

    /**
     * 参数内容
     */
    private String parameterValue;

    /**
     * 参数分组，用于分组分区域显示参数组
     */
    private String parameterGroup;

    public String getSkuid() {
        return skuid;
    }

    public void setSkuid(String skuid) {
        this.skuid = skuid == null ? null : skuid.trim();
    }

    public String getParameterid() {
        return parameterid;
    }

    public void setParameterid(String parameterid) {
        this.parameterid = parameterid == null ? null : parameterid.trim();
    }

    public String getParameterKey() {
        return parameterKey;
    }

    public void setParameterKey(String parameterKey) {
        this.parameterKey = parameterKey == null ? null : parameterKey.trim();
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue == null ? null : parameterValue.trim();
    }

    public String getParameterGroup() {
        return parameterGroup;
    }

    public void setParameterGroup(String parameterGroup) {
        this.parameterGroup = StringUtil.isEmpty(parameterGroup) ? "商品参数" : parameterGroup.trim();
    }

}
