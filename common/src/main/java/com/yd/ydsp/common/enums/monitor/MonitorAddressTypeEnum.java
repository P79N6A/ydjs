package com.yd.ydsp.common.enums.monitor;

/**
 * @author zengyixun
 * @date 17/11/27
 */
public enum MonitorAddressTypeEnum {
    /**
     * HLS流畅直播地址
     */
    LIVEADDRESS("liveAddress", 0),
    /**
     * HLS高清直播地址
     */
    HDADDRESS("hdAddress", 1),
    /**
     * RTMP流畅直播地址
     */
    RTMP("rtmp", 2),
    /**
     * RTMP高清直播地址
     */
    RTMPHD("rtmpHd", 3),
    ;

    public String name;

    public Integer status;

    MonitorAddressTypeEnum(String name, Integer status) {
        this.name = name;
        this.status = status;
    }

    public static Integer getStatusByName(String name) {
        Integer status = null;
        for (MonitorAddressTypeEnum compareEnum : MonitorAddressTypeEnum.values()) {
            if (compareEnum.getName().equals(name)) {
                status = compareEnum.getStatus();
            }
        }
        return status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
