package com.yd.ydsp.client.domian.paypoint;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class YdShopActivityInfoVO implements Serializable {

    /**
     * 店铺id
     */
    private String shopid;

    /**
     * 活动名称或者描述信息
     */
    private String activityName;

    /**
     * 活动id
     */
    private String activityid;

    /**
     * 活动页顶部资源类型 0-图片 1-点播视频 2-直播视频
     */
    private Integer resouceType;

    /**
     * 资源url
     */
    private String resouceUrl;

    /**
     * 活动详情
     */
    private List<PicVO> pics;

    /**
     * 活动创建年
     */
    private String yearInfo;

    /**
     * 活动创建月
     */
    private String monthInfo;

    /**
     * 0-正常开放；1-关闭(一但关闭，不可以再开放)；2-准备中(一但从准备中设置为正常开放状态，则不可以再为准备中)；
     */
    private Integer status=2;

    /**
     * 活动开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date beginDate;

    /**
     * 活动结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid == null ? null : shopid.trim();
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityid() {
        return activityid;
    }

    public void setActivityid(String activityid) {
        this.activityid = activityid == null ? null : activityid.trim();
    }

    public Integer getResouceType() {
        return resouceType;
    }

    public void setResouceType(Integer resouceType) {
        this.resouceType = resouceType;
    }

    public String getResouceUrl() {
        return resouceUrl;
    }

    public void setResouceUrl(String resouceUrl) {
        this.resouceUrl = resouceUrl == null ? null : resouceUrl.trim();
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<PicVO> getPics() {
        return pics;
    }

    public void setPics(List<PicVO> pics) {
        this.pics = pics;
    }

    public String getYearInfo() {
        return yearInfo;
    }

    public void setYearInfo(String yearInfo) {
        this.yearInfo = yearInfo == null ? null : yearInfo.trim();
    }

    public String getMonthInfo() {
        return monthInfo;
    }

    public void setMonthInfo(String monthInfo) {
        this.monthInfo = monthInfo == null ? null : monthInfo.trim();
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

}