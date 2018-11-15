package com.yd.ydsp.client.domian.openshop;

import java.io.Serializable;

public class YdShopHoursInfoVO {

    /**
     * 几点开始
     */
    private Integer beginH;

    /**
     * 几分,与beginH合为一个开始营业时间
     */
    private Integer beginM;

    /**
     * 几点结束
     */
    private Integer endH;

    /**
     * 几分,与endH合为一个结束营业时间
     */
    private Integer endM;

    /**
     * 星期几开始营业
     */
    private Integer beginDayofweek=1;

    /**
     * 星期几结束营业
     */
    private Integer endDayofweek=7;

    public Integer getBeginH() {
        return beginH;
    }

    public void setBeginH(Integer beginH) {
        this.beginH = beginH;
    }

    public Integer getEndH() {
        return endH;
    }

    public void setEndH(Integer endH) {
        this.endH = endH;
    }

    public Integer getBeginM() {
        return beginM;
    }

    public void setBeginM(Integer beginM) {
        this.beginM = beginM;
    }

    public Integer getEndM() {
        return endM;
    }

    public void setEndM(Integer endM) {
        this.endM = endM;
    }

    public Integer getBeginDayofweek() {
        if(beginDayofweek==null){
            return 1;
        }
        return beginDayofweek;
    }

    public void setBeginDayofweek(Integer beginDayofweek) {
        this.beginDayofweek = beginDayofweek;
    }

    public Integer getEndDayofweek() {
        if(endDayofweek==null){
            return 7;
        }
        return endDayofweek;
    }

    public void setEndDayofweek(Integer endDayofweek) {
        this.endDayofweek = endDayofweek;
    }
}
