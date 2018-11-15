package com.yd.ydsp.biz.config;

/**
 * @author zengyixun
 * @date 17/11/25
 */
public class DiamondYdXiaoerConfig extends AbstractDiamondConfig {
    public void init(){
        super.DATA_ID = "com.yd.ydsp.biz.config.DiamondYdXiaoerConfig";
        super.propertyInstance = DiamondYdXiaoerConfigHolder.getInstance();
        super.init();
    }
}
