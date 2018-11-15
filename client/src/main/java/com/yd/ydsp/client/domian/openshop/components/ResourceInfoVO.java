package com.yd.ydsp.client.domian.openshop.components;

import com.yd.ydsp.common.lang.StringUtil;
import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;
import java.util.Map;

public class ResourceInfoVO implements Serializable,Comparable<ResourceInfoVO> {
    /**
     * 0-图片；1-点播视频; 2-直播视频; 3-按钮(从ButtonInfoVO创建哈）
     */
    Integer resourceType;
    /**
     * resouce的资源url
     */
    String resourceUrl;

    /**
     * 资源名，可以为空
     */
    String resourceName;

    /**
     * 是指这个link是否可以点击跳转
     * 0-不跳转; 1-普通url; 2-商品; 3-专题页
     */
    Integer linkType;

    /**
     * 链接的值
     */
    String linkValue;

    /**
     * 此resouce在list中的位置(排序）
     */
    Integer sn;

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

    public Integer getResourceType() {
        return resourceType;
    }

    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceUrl() {
        if(StringUtil.isNotEmpty(this.resourceUrl)){
            return this.resourceUrl.replaceFirst("http://","https://");
        }
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
        if(StringUtil.isNotEmpty(this.resourceUrl)){
            this.resourceUrl =  this.resourceUrl.replaceFirst("http://","https://");
        }
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Integer getLinkType() {
        return linkType;
    }

    public void setLinkType(Integer linkType) {
        this.linkType = linkType;
    }

    public String getLinkValue() {
//        if(StringUtil.isNotEmpty(this.linkValue)){
//            return StringUtil.replaceChars(this.linkValue,"http://","https://");
//        }
        return linkValue;
    }

    public void setLinkValue(String linkValue) {
        this.linkValue = linkValue;
//        if(StringUtil.isNotEmpty(this.linkValue)){
//            this.linkValue = StringUtil.replaceChars(this.linkValue,"http://","https://");
//        }
    }

    public Integer getSn() {
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    @Override
    public int compareTo(ResourceInfoVO o) {
        return this.getSn().intValue() - o.getSn().intValue();
    }
}
