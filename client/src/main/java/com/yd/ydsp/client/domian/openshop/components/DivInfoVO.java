package com.yd.ydsp.client.domian.openshop.components;

import org.htmlparser.tags.Div;

import java.io.Serializable;
import java.util.List;

public class DivInfoVO implements Serializable,Comparable<DivInfoVO> {
    /**
     * 块序号
     */
    Integer sn;
    /**
     * 块标题名称，可以为空
     */
    String titleName;
    /**
     * 块的排版类型
     */
    Integer typeset;

    /**
     * 块内资源列表
     */
    List<ResourceInfoVOExt> resourceInfoVOS;

    public Integer getSn() {
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public Integer getTypeset() {
        return typeset;
    }

    public void setTypeset(Integer typeset) {
        this.typeset = typeset;
    }

    public List<ResourceInfoVOExt> getResourceInfoVOS() {
        return resourceInfoVOS;
    }

    public void setResourceInfoVOS(List<ResourceInfoVOExt> resourceInfoVOS) {
        this.resourceInfoVOS = resourceInfoVOS;
    }

    @Override
    public int compareTo(DivInfoVO o) {
        return this.getSn().intValue() - o.getSn().intValue();
    }
}
