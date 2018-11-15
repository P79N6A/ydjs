package com.yd.ydsp.common.enums.monitor;

/**
 * Created by zengyixun on 17/8/18.
 */
public enum MonitorFlagConstants {
    NVR(1 << 1, "硬件录像机"),
    ;

    private Integer value;

    private String name;

    MonitorFlagConstants(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
