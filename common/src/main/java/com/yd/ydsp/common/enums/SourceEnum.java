package com.yd.ydsp.common.enums;

/**
 * Created by zengyixun on 17/4/4.
 */
public enum SourceEnum {
    WEIXIN2C("WEIXIN2C", 0),
    ROOTMANAGER("ROOTMANAGER", 6),
    WEIXIN2B("WEIXIN2B", 8),
    WEIXINXIAOER("WEIXINXIAOER", 18),
    WEIXINAGENT("WEIXINAGENT", 16),
    WEIXINSMALL("WEIXINSMALL", 1),
    WEIXINAPPSERVICE("WEIXINAPPSERVICE", 3),
    WEIXINAPPPUBLIC("WEIXINAPPPUBLIC", 5),
    DINGDING("DINGDING", 2),
    YDJSH5("YDJSH5", 9),
    ;

    public String name;

    public Integer type;

    SourceEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static Integer getTypeByName(String name) {
        Integer type = null;
        for (SourceEnum compareEnum : SourceEnum.values()) {
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


    public static SourceEnum nameOf(String name) {

        for (SourceEnum compareEnum : SourceEnum.values()) {
            if (compareEnum.getName().equals(name.trim().toUpperCase())) {
                return compareEnum;
            }

        }
        return null;
    }

    public static SourceEnum nameOf(Integer value){
        if(value==null){
            return null;
        }
        for (SourceEnum compareEnum : SourceEnum.values()) {
            if (compareEnum.getType().intValue()==value.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }

}
