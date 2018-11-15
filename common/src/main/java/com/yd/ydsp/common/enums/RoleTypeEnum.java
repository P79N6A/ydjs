package com.yd.ydsp.common.enums;

/**
 * Created by zengyixun on 17/5/11.
 */
public enum RoleTypeEnum {
    NORMAL("normal", 0),//一般用户
    DEVICEOWNER("DeviceOwner", 1),//设备注册者成为此设备的owner
    DEVICEMANAGER("DeviceManager", 2),//可以对设备进行技术支持的员工
    ROOT("root",999),//超级用户
    ;

    public String name;

    public Integer type;

    RoleTypeEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static Integer getTypeByName(String name) {
        Integer type = null;
        for (RoleTypeEnum compareEnum : RoleTypeEnum.values()) {
            if (compareEnum.getName().equals(name)) {
                type = compareEnum.getType();
            }
        }
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
