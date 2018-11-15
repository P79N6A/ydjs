package com.yd.ydsp.biz.user.model;

public enum CheckMobileCodeTypeEnum {
    BINDMOBILE("bindMobile", 1,"一般可通用性的手机验证"),
    BINDSHOPMOBILE("bindShopMobile", 2,"申请店铺入驻时手机验证"),
    BINDOWNERMOBILE("bindOwnerMobile", 3,"负责人进行信息变更时验证手机"),
    BINDNEWOWNERMOBILE("bindNewOwnerMobile", 4,"负责人进行信息变更时验证手机"),
    BINDNMANAGEMOBILE("bindManageMobile", 5,"管理员进行手机密码绑定时验证手机"),
    ;

    public String name;

    public Integer type;

    public String desc;

    CheckMobileCodeTypeEnum(String name, Integer type, String desc) {
        this.name = name;
        this.type = type;
        this.desc = desc;
    }

    public static Integer getTypeByName(String name) {
        Integer value = null;
        for (CheckMobileCodeTypeEnum compareEnum : CheckMobileCodeTypeEnum.values()) {
            if (compareEnum.getName().equals(name)) {
                value = compareEnum.getType();
            }
        }
        return value;
    }

    public static String getDesc(String name) {
        String desc = null;
        for (CheckMobileCodeTypeEnum compareEnum : CheckMobileCodeTypeEnum.values()) {
            if (compareEnum.getName().equals(name)) {
                desc = compareEnum.getDesc();
            }
        }
        return desc;
    }

    public static String getDesc(Integer type) {
        String desc = null;
        for (CheckMobileCodeTypeEnum compareEnum : CheckMobileCodeTypeEnum.values()) {
            if (compareEnum.getType().intValue()==type.intValue()) {
                desc = compareEnum.getDesc();
            }
        }
        return desc;
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

    public String getDesc(){ return desc; }

    public void setDesc(String desc){ this.desc = desc; }

    public static CheckMobileCodeTypeEnum nameOf(String name) {

        for (CheckMobileCodeTypeEnum compareEnum : CheckMobileCodeTypeEnum.values()) {
            if (compareEnum.getName().equals(name)) {
                return compareEnum;
            }

        }
        return null;
    }

    public static CheckMobileCodeTypeEnum nameOf(Integer type){
        if(type==null){
            return null;
        }
        for (CheckMobileCodeTypeEnum compareEnum : CheckMobileCodeTypeEnum.values()) {
            if (compareEnum.getType().intValue()==type.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }
}
