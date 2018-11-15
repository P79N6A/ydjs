package com.yd.ydsp.client.domian.paypoint;

import com.yd.ydsp.client.domian.CPDeviceInfoVO;

/**
 * @author zengyixun
 * @date 18/1/11
 */
public class PrinterInfoVO extends CPDeviceInfoVO {

    /**
     * 打印机类型：比如是后厨还是接单打印机，对应的小票内容不同
     */
    private String printType;
    /**
     * 打印机类型的中文名称，给前端显示使用
     */
    private String printTypeName;

    private String key;


    /**
     * 打印次数设置，用于表达，一次订单在同一打印机上要打出几张票来，默认为1
     */
    private Integer times=1;

    public String getPrintType(){ return printType; }
    public void setPrintType(String printType){ this.printType = printType;}

    public String getPrintTypeName(){ return printTypeName; }
    public void setPrintTypeName(String printTypeName){ this.printTypeName = printTypeName; }

    public Integer getTimes(){ return times; }
    public void setTimes(Integer times){
        if(times==null){
            this.times = 1;
        }else {
            this.times = times;
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
