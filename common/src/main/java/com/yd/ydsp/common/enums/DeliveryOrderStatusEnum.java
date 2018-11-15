package com.yd.ydsp.common.enums;

public enum DeliveryOrderStatusEnum {

    WaiteDispatch("待派单", 1),
    WaitePickUpGood("待取货", 2),
    Delivery("配送中", 3),
    Finish("已完成", 4),
    Cancel("已取消", 5),
    TimeOut("已过期", 7),
    AssignOrder("指派单", 8),
    ExceptionOrderComeBack("妥投异常之物品返回中",9),
    ExceptionOrderComeBackOver("妥投异常之物品返回完成",10),
    Fail("创建运单失败",1000),
    ;

    public String name;

    public Integer status;

    DeliveryOrderStatusEnum(String name, Integer status) {
        this.name = name;
        this.status = status;
    }

    public static Integer getTypeByName(String name) {
        Integer status = null;
        for (DeliveryOrderStatusEnum compareEnum : DeliveryOrderStatusEnum.values()) {
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

    public static DeliveryOrderStatusEnum nameOf(Integer status){
        if(status==null){
            return null;
        }
        for (DeliveryOrderStatusEnum compareEnum : DeliveryOrderStatusEnum.values()) {
            if (compareEnum.getStatus().intValue()==status.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }
    
}
