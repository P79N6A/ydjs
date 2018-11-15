package com.yd.ydsp.client.domian.paypoint;

import java.io.Serializable;

public class WorkerInfoVO implements Serializable {
    /**
     * 店铺id
     */
    private String shopid;

    /**
     * 姓名
     */
    private String workerName;

    /**
     * 手机
     */
    private String workerMobile;

    /**
     * 手机脱敏，用于显示
     */
    private String showMobile;

    /**
     * 1-负责人
     * 2-管理员
     * 3-服务员
     */
    private Integer workerType;

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getWorkerMobile() {
        return workerMobile;
    }

    public void setWorkerMobile(String workerMobile) {
        this.workerMobile = workerMobile;
    }

    public Integer getWorkerType() {
        return workerType;
    }

    public void setWorkerType(Integer workerType) {
        this.workerType = workerType;
    }

    public String getShowMobile() {
        return showMobile;
    }

    public void setShowMobile(String showMobile) {
        this.showMobile = showMobile;
    }
}
