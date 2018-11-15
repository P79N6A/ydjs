package com.yd.ydsp.biz.cp.model;

import com.yd.ydsp.common.redis.SerializeUtils;

import java.util.List;
import java.util.Map;

/**
 * @author zengyixun
 * @date 17/12/5
 */
public class CPMonitorVO extends SerializeUtils {

    /**
     * deviceSerial下可能有多个通道
     */
    Map<String,List<Integer>> deviceSerials;

    /**
     * 设备名称
     */
    Map<String ,String> deviceSerialNames;

    String name;

    String shopid;

    String ossPath;

    String shopImage1;
    String shopImage2;
    List<String> shopImag3;

    List<String> shopImagDaily;

    List<String> shopDesc;

    String tel;

    public Map<String,List<Integer>> getDeviceSerials(){ return deviceSerials; }
    public void setDeviceSerials(Map<String,List<Integer>> deviceSerials){
        this.deviceSerials = deviceSerials;
    }

    public Map<String,String> getDeviceSerialNames(){ return deviceSerialNames; }
    public void setDeviceSerialNames(Map<String,String> deviceSerialNames){ this.deviceSerialNames = deviceSerialNames; }

    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }

    public String getShopid(){ return shopid; }
    public void setShopid(String shopid){ this.shopid = shopid; }

    public String getOssPath() {
        return ossPath;
    }
    public void setOssPath(String ossPath) {
        this.ossPath = ossPath == null ? null : ossPath.trim();
    }

    public String getShopImage1(){ return shopImage1; }
    public void setShopImage1(String shopImage1){ this.shopImage1 = shopImage1; }

    public String getShopImage2(){ return shopImage2; }
    public void setShopImage2(String shopImage2){ this.shopImage2 = shopImage2; }

    public List<String> getShopImag3(){ return shopImag3;}
    public void setShopImag3(List<String> shopImag3){ this.shopImag3 = shopImag3; }

    public List<String> getShopImagDaily(){ return shopImagDaily;}
    public void setShopImagDaily(List<String> shopImagDaily){ this.shopImagDaily = shopImagDaily; }

    public List<String> getShopDesc(){ return shopDesc;}
    public void setShopDesc(List<String> shopDesc){ this.shopDesc = shopDesc; }

    public String getTel(){return tel; }
    public void setTel(String tel){ this.tel = tel; }


}
