package com.yd.ydsp.client.domian.openshop.components;

import java.io.Serializable;

public class ResourceInfoVOExt extends ResourceInfoVO implements Serializable {

    /**
     * 扩展数据，比如当linkType为商品类型时，这里需要存放WareSkuBaseVO对象转为Map后的键值
     */
    Object extData;

    public Object getExtData() {
        return extData;
    }

    public void setExtData(Object extData) {
        this.extData = extData;
    }
}
