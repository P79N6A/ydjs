package com.yd.ydsp.common.enums.manage;

/**
 * Created by zengyixun on 17/4/4.
 */
public enum AgentApplyStatusEnum {
    NOINTENTION("nointention",-2),//无意向
    NULL("null", -1),//空的申请
    APPLY("apply", 0),//申请中
    AUDIT("audit", 1),//审核中
    WAITE("waite", 2),//等待签约
    MANAGER("manager", 7),//不能出在在数据库字段中，这个只是用来表示一个人是此加盟商的管理员
    FINISH("finish",8),//完成签约成为了加盟商
    AUDIT2("audit2",9),//完成签约后，要修改资料进行审核
    ;

    public String name;

    public Integer status;

    AgentApplyStatusEnum(String name, Integer status) {
        this.name = name;
        this.status = status;
    }

    public static Integer getStatusByName(String name) {
        Integer status = null;
        for (AgentApplyStatusEnum compareEnum : AgentApplyStatusEnum.values()) {
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

    public static AgentApplyStatusEnum nameOf(String name) {

        for (AgentApplyStatusEnum compareEnum : AgentApplyStatusEnum.values()) {
            if (compareEnum.getName().equals(name.trim().toUpperCase())) {
                return compareEnum;
            }

        }
        return null;
    }

    public static AgentApplyStatusEnum nameOf(Integer value){
        if(value==null){
            return null;
        }
        for (AgentApplyStatusEnum compareEnum : AgentApplyStatusEnum.values()) {
            if (compareEnum.getStatus().intValue()==value.intValue()) {
                return compareEnum;
            }

        }
        return null;

    }

}
