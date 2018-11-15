package com.yd.ydsp.biz.config;

/**
 * @author zengyixun
 * @date 17/11/25
 */
public class DiamondYdWareConfig extends AbstractDiamondConfig {
    public void init(){
        super.DATA_ID = "com.yd.ydsp.biz.config.DiamondYdWareConfig";
        super.propertyInstance = DiamondYdWareConfigHolder.getInstance();
        super.init();
    }
}
