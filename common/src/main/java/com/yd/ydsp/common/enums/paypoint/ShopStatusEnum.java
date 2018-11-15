package com.yd.ydsp.common.enums.paypoint;

/**
 * Created by zengyixun on 17/8/13.
 */
public enum ShopStatusEnum {
    SUSPEND("SUSPEND",-1),//停止使用
    APPLY("APPLY", 0),//申请，资料填写完成
    SETMEAL("SETMEAL", 1),//选择了套餐
    TIMEOUT("TIMEOUT", 2),//套餐过期
    NORMAL("NORMAL", 3),//正常营业中
    PAUSE("PAUSE",4), //暂时未营业
    ;

    public String name;

    public Integer type;

    ShopStatusEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static Integer getTypeByName(String name) {
        Integer type = null;
        for (ShopStatusEnum compareEnum : ShopStatusEnum.values()) {
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
    public static ShopStatusEnum nameOf(String value){
        for (ShopStatusEnum compareEnum : ShopStatusEnum.values()) {
            if (compareEnum.getName().equals(value)) {
                return compareEnum;
            }

        }
        return null;

    }

    public static ShopStatusEnum nameOf(Integer value){
        if(value==null){
            return null;
        }
        for (ShopStatusEnum compareEnum : ShopStatusEnum.values()) {
            if (compareEnum.getType().intValue()==value.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }
}
