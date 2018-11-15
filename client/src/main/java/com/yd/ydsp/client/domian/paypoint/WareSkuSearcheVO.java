package com.yd.ydsp.client.domian.paypoint;

import java.io.Serializable;

/**
 * @author zengyixun
 * @date 18/1/8
 */
public class WareSkuSearcheVO extends WareSkuVO implements Serializable {

    private String wareitemName;
    private Integer wareitemStatus;

    public String getWareitemName() {
        return wareitemName;
    }

    public void setWareitemName(String wareitemName) {
        this.wareitemName = wareitemName;
    }

    public Integer getWareitemStatus() {
        return wareitemStatus;
    }

    public void setWareitemStatus(Integer wareitemStatus) {
        this.wareitemStatus = wareitemStatus;
    }
}
