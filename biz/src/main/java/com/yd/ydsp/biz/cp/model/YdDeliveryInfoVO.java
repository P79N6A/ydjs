package com.yd.ydsp.biz.cp.model;

import com.yd.ydsp.common.redis.SerializeUtils;

/**
 * @author zengyixun
 * @date 17/12/28
 */
public class YdDeliveryInfoVO extends SerializeUtils {

    /**
     * id
     */
    Integer id;

    /**
     * 物流方式名称
     */
    String deliveryName;

    /**
     * 物流方式对应的图片地址
     */
    String deliveryImageUrl;

    /**
     * 描述信息
     */
    String desc;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    public String getDeliveryImageUrl() {
        return deliveryImageUrl;
    }

    public void setDeliveryImageUrl(String deliveryImageUrl) {
        this.deliveryImageUrl = deliveryImageUrl;
    }

    public String getDesc(){ return desc; }
    public void setDesc(String desc){ this.desc = desc; }


}
