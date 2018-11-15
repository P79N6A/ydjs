package com.yd.ydsp.biz.config;

/**
 * @author zengyixun
 * @date 17/11/25
 */
public class DiamondYdSystemConfig extends AbstractDiamondConfig {
    public void init(){
        super.DATA_ID = "com.yd.ydsp.biz.config.DiamondYdSystemConfig";
        super.propertyInstance = DiamondYdSystemConfigHolder.getInstance();
        super.init();
    }
}
