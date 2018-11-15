package com.yd.ydsp.client.domian.openshop.components;

import java.io.Serializable;
import java.util.List;

public class CustomShowItemVO implements Serializable,Comparable<CustomShowItemVO> {
    /**
     * 序号
     */
    Integer sn=1;
    /**
     * 名称
     */
    String showName;

    String backColor;

    /**
     * 头部展示图，可以为空，为空则显示showName
     */
    String titlePic;

    /**
     * 是否开放，如果为false表明此展示区要隐藏
     */
    Boolean open=true;

    /**
     * 用户自定义区块列表
     */
    List<DivInfoVO> divInfoList;

    public Integer getSn() {
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    public String getTitlePic() {
        return titlePic;
    }

    public void setTitlePic(String titlePic) {
        this.titlePic = titlePic;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public List<DivInfoVO> getDivInfoList() {
        return divInfoList;
    }

    public void setDivInfoList(List<DivInfoVO> divInfoList) {
        this.divInfoList = divInfoList;
    }

    @Override
    public int compareTo(CustomShowItemVO o) {
        return this.getSn().intValue() - o.getSn().intValue();
    }
}
