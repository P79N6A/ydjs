package com.yd.ydsp.biz.deliver.model;

import java.io.Serializable;

public class DaDaCityData implements Serializable {

    String cityName;
    String cityCode;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }
}
