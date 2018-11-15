package com.yd.ydsp.client.domian.paypoint;

import java.io.Serializable;

/**
 * @author zengyixun
 * @date 18/1/8
 */
public class PicVO implements Serializable {

    /**
     * 排序字段
     */
    private Integer sn;

    /**
     * 图片url
     */
    private String picUrl;

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl==null?null:picUrl.trim();
    }

    public Integer getSn() {
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }
}
