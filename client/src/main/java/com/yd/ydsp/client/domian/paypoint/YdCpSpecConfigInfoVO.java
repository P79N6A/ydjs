package com.yd.ydsp.client.domian.paypoint;

import java.io.Serializable;

public class YdCpSpecConfigInfoVO extends YdCpSpecConfigBaseInfoVO implements Serializable {

    /**
     * 商品skuid
     */
    private String skuid;

    public String getSkuid() {
        return skuid;
    }

    public void setSkuid(String skuid) {
        this.skuid = skuid == null ? null : skuid.trim();
    }

}
