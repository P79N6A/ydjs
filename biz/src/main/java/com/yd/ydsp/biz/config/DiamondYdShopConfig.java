package com.yd.ydsp.biz.config;

/**
 * @author zengyixun
 * @date 17/11/25
 */
public class DiamondYdShopConfig extends AbstractDiamondConfig {
    public void init(){
        super.DATA_ID = "com.yd.ydsp.biz.config.DiamondYdShopConfig";
        super.propertyInstance = DiamondYdShopConfigHolder.getInstance();
        super.init();
    }
}
