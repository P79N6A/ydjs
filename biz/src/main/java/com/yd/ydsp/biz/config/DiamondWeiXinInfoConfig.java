package com.yd.ydsp.biz.config;

/**
 * @author zengyixun
 * @date 17/11/25
 */
public class DiamondWeiXinInfoConfig extends AbstractDiamondConfig {
    public void init(){
        super.DATA_ID = "com.yd.ydsp.biz.config.DiamondWeiXinInfoConfig";
        super.propertyInstance = DiamondWeiXinInfoConfigHolder.getInstance();
        super.init();
    }
}
