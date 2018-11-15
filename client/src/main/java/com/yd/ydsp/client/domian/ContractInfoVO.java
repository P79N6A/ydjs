package com.yd.ydsp.client.domian;

import java.io.Serializable;
import java.util.Date;

public class ContractInfoVO implements Serializable {

    /**
     * 合约名称
     */
    private String contractName;

    /**
     * 合约id
     */
    private String contractId;

    /**
     * 合约内容的url
     */
    private String contractUrl;

    /**
     * 合约场景
     */
    private String contractScene;

    /**
     * 签约时间
     */
    private Date modifyDate;

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getContractUrl() {
        return contractUrl;
    }

    public void setContractUrl(String contractUrl) {
        this.contractUrl = contractUrl;
    }

    public String getContractScene() {
        return contractScene;
    }

    public void setContractScene(String contractScene) {
        this.contractScene = contractScene;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }
}
