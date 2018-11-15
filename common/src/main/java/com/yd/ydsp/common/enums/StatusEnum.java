package com.yd.ydsp.common.enums;

/**
 * Created by zengyixun on 17/4/4.
 */
public enum StatusEnum {
    NORMAL("NORMAL", 0),
    DELETE("DELETE", 1),
    FREEZE("FREEZE", 2),
    RUNNING("RUNNING", 3),
    SUSPEND("SUSPEND", 4),
    UNDERREPAIR("UNDERREPAIR",5),
    ;

    public String name;

    public Integer status;

    StatusEnum(String name, Integer status) {
        this.name = name;
        this.status = status;
    }

    public static Integer getStatusByName(String name) {
        Integer status = null;
        for (StatusEnum compareEnum : StatusEnum.values()) {
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
