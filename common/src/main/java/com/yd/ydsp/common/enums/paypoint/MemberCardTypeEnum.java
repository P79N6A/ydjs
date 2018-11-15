package com.yd.ydsp.common.enums.paypoint;

/**
 * 会员卡类型
 */
public enum MemberCardTypeEnum {
    LEVEL0("level0", 0,"大众会员","http://static.ydjs360.com/cp/image/dazhong.png"),
    LEVEL1("level1", 1,"黄金会员","http://static.ydjs360.com/cp/image/huangjing.png"),
    LEVEL2("level2", 2,"铂金会员","http://static.ydjs360.com/cp/image/baijing.png"),
    LEVEL3("level3", 3,"钻石会员","http://static.ydjs360.com/cp/image/zhuangsi.png"),
    ;

    public String name;

    public Integer type;

    public String desc;

    public String picUrl;

    MemberCardTypeEnum(String name, Integer type,String desc,String picUrl) {
        this.name = name;
        this.type = type;
        this.desc = desc;
        this.picUrl = picUrl;
    }

    public static Integer getTypeByName(String name) {
        Integer value = null;
        for (MemberCardTypeEnum compareEnum : MemberCardTypeEnum.values()) {
            if (compareEnum.getName().equals(name)) {
                value = compareEnum.getType();
            }
        }
        return value;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public static MemberCardTypeEnum nameOf(String name) {

        for (MemberCardTypeEnum compareEnum : MemberCardTypeEnum.values()) {
            if (compareEnum.getName().equals(name)) {
                return compareEnum;
            }

        }
        return null;
    }

    public static MemberCardTypeEnum nameOf(Integer type){
        if(type==null){
            return null;
        }
        for (MemberCardTypeEnum compareEnum : MemberCardTypeEnum.values()) {
            if (compareEnum.getType().intValue()==type.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }

    public static Integer getCount(){
        return MemberCardTypeEnum.values().length;
    }
}
