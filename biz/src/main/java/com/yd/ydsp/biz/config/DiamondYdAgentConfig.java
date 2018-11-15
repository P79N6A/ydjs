package com.yd.ydsp.biz.config;

/**
 * @author zengyixun
 * @date 17/11/25
 */
public class DiamondYdAgentConfig extends AbstractDiamondConfig {
    public void init(){
        super.DATA_ID = "com.yd.ydsp.biz.config.DiamondYdAgentConfig";
        super.propertyInstance = DiamondYdAgentConfigHolder.getInstance();
        super.init();
    }
}
