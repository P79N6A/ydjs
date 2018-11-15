package com.yd.ydsp.biz.config;

import com.yd.ydsp.common.lang.StringUtil;

/**
 * @author zengyixun
 * @date 17/11/25
 */
public class DiamondYdWareConfigHolder {
    static final DiamondYdWareConfigHolder instance = new DiamondYdWareConfigHolder();
    public static DiamondYdWareConfigHolder getInstance(){ return instance; }

    /**
     * 系统自带的单位
     */
    public String sysUnits = "个,份,瓶,包,箱,盒,克,千克,斤";

    /**
     * 商家最多能推荐的商品数量
     */
    public Integer commend = 20;

    public Integer mustWare = 10;

    public String defaultGoodsPicUrl = "";

    public String getSysUnits() {
        return sysUnits;
    }

    public void setSysUnits(String sysUnits) {
        if(StringUtil.isNotEmpty(sysUnits)){
            sysUnits = sysUnits.trim();
        }
        this.sysUnits = sysUnits;
    }

    public Integer getCommend() {
        return commend;
    }

    public void setCommend(Integer commend) {
        this.commend = commend;
    }

    public Integer getMustWare() {
        return mustWare;
    }

    public void setMustWare(Integer mustWare) {
        this.mustWare = mustWare;
    }

    public String getDefaultGoodsPicUrl() {
        return defaultGoodsPicUrl;
    }

    public void setDefaultGoodsPicUrl(String defaultGoodsPicUrl) {
        this.defaultGoodsPicUrl = defaultGoodsPicUrl;
    }
}
