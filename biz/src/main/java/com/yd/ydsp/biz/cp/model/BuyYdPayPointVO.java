package com.yd.ydsp.biz.cp.model;

import java.io.Serializable;

/**
 * Created by zengyixun on 17/9/17.
 */
public class BuyYdPayPointVO implements Serializable {

    private String buyCaseName;

    private Integer tableNum;

    private Integer printerNum;

    private Integer monitorNum;

    private Integer posNum;

    public String getBuyCaseName(){ return buyCaseName; }
    public void setBuyCaseName(String buyCaseName){ this.buyCaseName = buyCaseName; }

    public Integer getTableNum(){ return tableNum; }
    public void setTableNum(Integer tableNum){ this.tableNum = tableNum; }

    public Integer getPrinterNum(){ return printerNum; }
    public void setPrinterNum(Integer printerNum){ this.printerNum = printerNum; }

    public Integer getMonitorNum(){ return monitorNum; }
    public void setMonitorNum(Integer monitorNum){ this.monitorNum = monitorNum; }

    public Integer getPosNum(){ return posNum; }
    public void setPosNum(Integer posNum){ this.posNum = posNum; }

}
