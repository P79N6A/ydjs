package com.yd.ydsp.common.enums.manage;

/**
 * Created by zengyixun on 17/4/4.
 */
public enum ShopApplyStatusEnum {
    NEW("NEW", 0),//新申请
//    WAITE("WAITE", 1),//等着授权公众号或者小程序
    CREATING("CREATING", 2),//创建商城中
    FINISH("FINISH", 3),//完成店铺上线
    ;

    public String name;

    public Integer status;

    ShopApplyStatusEnum(String name, Integer status) {
        this.name = name;
        this.status = status;
    }

    public static Integer getStatusByName(String name) {
        Integer status = null;
        for (ShopApplyStatusEnum compareEnum : ShopApplyStatusEnum.values()) {
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

    public static ShopApplyStatusEnum nameOf(Integer value){
        if(value==null){
            return null;
        }
        for (ShopApplyStatusEnum compareEnum : ShopApplyStatusEnum.values()) {
            if (compareEnum.getStatus().intValue()==value.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }

}
