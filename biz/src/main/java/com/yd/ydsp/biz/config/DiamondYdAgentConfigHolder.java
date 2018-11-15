package com.yd.ydsp.biz.config;

/**
 * @author zengyixun
 * @date 18/06/19
 */
public class DiamondYdAgentConfigHolder {
    static final DiamondYdAgentConfigHolder instance = new DiamondYdAgentConfigHolder();
    public static DiamondYdAgentConfigHolder getInstance(){ return instance; }

    /**
     * 手机验证码过期时间
     */
    public Integer bindMobileExpin = 300;

    /**
     * 登录会话过期时间
     */
    public Integer loginExpin = 28800;

    public String loginErrorPageUrl="http://www.ydjs360.com/";

    public String agentMainPageUrl="http://www.ydjs360.com/";

    public String agentApplyPageUrl="http://www.ydjs360.com/";

    public String bindMobilePageUrl="http://www.ydjs360.com/";

    public Integer getBindMobileExpin() {
        return bindMobileExpin;
    }

    public void setBindMobileExpin(Integer bindMobileExpin) {
        this.bindMobileExpin = bindMobileExpin;
    }

    public Integer getLoginExpin() {
        return loginExpin;
    }

    public void setLoginExpin(Integer loginExpin) {
        this.loginExpin = loginExpin;
    }

    public String getLoginErrorPageUrl() {
        return loginErrorPageUrl;
    }

    public void setLoginErrorPageUrl(String loginErrorPageUrl) {
        this.loginErrorPageUrl = loginErrorPageUrl;
    }

    public String getAgentMainPageUrl() {
        return agentMainPageUrl;
    }

    public void setAgentMainPageUrl(String agentMainPageUrl) {
        this.agentMainPageUrl = agentMainPageUrl;
    }

    public String getAgentApplyPageUrl() {
        return agentApplyPageUrl;
    }

    public void setAgentApplyPageUrl(String agentApplyPageUrl) {
        this.agentApplyPageUrl = agentApplyPageUrl;
    }

    public String getBindMobilePageUrl() {
        return bindMobilePageUrl;
    }

    public void setBindMobilePageUrl(String bindMobilePageUrl) {
        this.bindMobilePageUrl = bindMobilePageUrl;
    }
}
