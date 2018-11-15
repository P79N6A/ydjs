package com.yd.ydsp.biz.config;

/**
 * @author zengyixun
 * @date 17/11/15
 */
public class DiamondYdPayPointConfig extends AbstractDiamondConfig {

    public void init(){
        super.DATA_ID = "com.yd.ydsp.biz.config.DiamondYdPayPointConfig";
        super.propertyInstance = DiamondYdPayPointConfigHolder.getInstance();
        super.init();
    }

}
