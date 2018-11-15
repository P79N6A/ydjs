package com.yd.ydsp.client.domian.manage;

import java.io.Serializable;

public class AgentWorkerInfoVO implements Serializable {
    /**
     * id
     */
    private String agentid;

    /**
     * 姓名
     */
    private String workerName;

    /**
     * 手机
     */
    private String workerMobile;

    /**
     * 1-负责人
     * 2-管理员
     * 3-一般人员
     */
    private Integer workerType;

    public String getAgentid() {
        return agentid;
    }

    public void setAgentid(String agentid) {
        this.agentid = agentid;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getWorkerMobile() {
        return workerMobile;
    }

    public void setWorkerMobile(String workerMobile) {
        this.workerMobile = workerMobile;
    }

    public Integer getWorkerType() {
        return workerType;
    }

    public void setWorkerType(Integer workerType) {
        this.workerType = workerType;
    }
}
