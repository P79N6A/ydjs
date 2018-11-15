package com.yd.ydsp.biz.weixin.model.message;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by zengyixun on 17/8/25.
 */

@XmlRootElement(name = "xml")
public class LocationEvent extends ReceiveBaseEventMsg {
    /**
     * 地理位置纬度
     */
    @XmlElement(name = "Latitude")
    private String Latitude;
    /**
     * 地理位置经度
     */
    @XmlElement(name = "Longitude")
    private String Longitude;
    /**
     * 地理位置精度
     */
    @XmlElement(name = "Precision")
    private String Precision;

    public LocationEvent() {
        super();
    }

    public String getLatitude(){ return Latitude;}
    public void setLatitude(String Latitude){this.Latitude = Latitude;}

    public String getLongitude(){ return Longitude;}
    public void setLongitude(String Longitude){ this.Longitude = Longitude;}

    public String getPrecision(){ return Precision;}
    public void setPrecision(String Precision){ this.Precision = Precision;}

}
