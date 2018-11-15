package com.yd.ydsp.client.domian.paypoint;

import com.yd.ydsp.common.constants.paypoint.ShopPageFlagConstants;
import com.yd.ydsp.common.utils.FeatureUtil;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class YdShopActivityInfoDTO {
    private Long id;

    private String shopid;

    private String activityName;

    private String activityid;

    private Integer resouceType;

    private String resouceUrl;

    private String picList;

    private Long flag=0L;

    private String feature;
    protected Map<String, String> featureMap;

    private String feature1;

    private String feature2;

    private String feature3;

    private String feature4;

    private String feature5;

    private String feature6;

    private String feature7;

    private String feature8;

    private String feature9;

    private String yearInfo;

    private String monthInfo;

    private Integer status=0;

    private Date beginDate;

    private Date endDate;

    private Date createDate;

    private Date modifyDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        this.activityName = activityName == null ? null : activityName.trim();
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

    public String getPicList() {
        return picList;
    }

    public void setPicList(String picList) {
        this.picList = picList == null ? null : picList.trim();
    }

    public Long getFlag() {
        return flag;
    }

    public void setFlag(Long flag) {
        this.flag = flag;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
    }
    public void removeAllFeature() {
        this.featureMap = null;
        this.feature = null;
    }
    private void resetFeature() {
        this.feature = FeatureUtil.toString(featureMap);
    }
    private void initFeatureMap() {
        if (null == featureMap) {
            featureMap = this.getFeatureMap(feature);
        }
    }

    public Map<String, String> getFeatureMap() {
        return getFeatureMap(feature);
    }

    public void setFeatureMap(Map<String, String> featureMap) {
        this.featureMap = featureMap;
    }

    private Map<String, String> getFeatureMap(String features) {
        return FeatureUtil.toMap(feature);

    }
    /**
     * 添加一个特定的key-value
     *
     * @param key
     * @param value
     */
    public void addFeature(String key, String value) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value) && !"null".equals(value)) {
            initFeatureMap();
            featureMap.put(key, value);
            resetFeature();
        }
    }

    /**
     * value可能是一个列表
     * @param key
     * @param value
     */
    public void addListFeature(String key, String value) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value) && !"null".equals(value)) {
            List<String> list = getListFeature(key);
            if(list==null || list.size()==0){
                initFeatureMap();
                featureMap.put(key, value);
                resetFeature();
                return;
            }
            if(list!=null && list.size()>0 && !list.contains(value)){

                list.add(value);
                initFeatureMap();
                featureMap.put(key, FeatureUtil.listToString(list));
                resetFeature();
                return;
            }
        }
    }

    /**
     * @param key
     * @param value
     */
    public void delListFeature(String key, String value) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value) && !"null".equals(value)) {
            List<String> list = getListFeature(key);
            if(list==null || list.size()==0){
                return;
            }
            if(list!=null && list.size()>0 && list.contains(value)){

                list.remove(value);
                initFeatureMap();
                featureMap.put(key, FeatureUtil.listToString(list));
                resetFeature();
                return;
            }
        }
    }

    /**
     * 根据一个key,获取一个value
     *
     * @param key
     * @return
     */
    public String getFeature(String key) {
        initFeatureMap();
        String value = featureMap.get(key);
        return value == null ? null : value;
    }


    /**
     * 根据一个key,获取一个List类型的value
     *
     * @param key
     * @return
     */
    public List<String> getListFeature(String key) {
        initFeatureMap();
        String value = featureMap.get(key);
        return value == null ? null : FeatureUtil.strToList(value);
    }

    public boolean removeFeature(String key) {
        initFeatureMap();
        boolean flag = false;
        if (featureMap.containsKey(key)) {
            featureMap.remove(key);
            resetFeature();
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    public String getFeature1() {
        return feature1;
    }

    public void setFeature1(String feature1) {
        this.feature1 = feature1 == null ? null : feature1.trim();
    }

    public String getFeature2() {
        return feature2;
    }

    public void setFeature2(String feature2) {
        this.feature2 = feature2 == null ? null : feature2.trim();
    }

    public String getFeature3() {
        return feature3;
    }

    public void setFeature3(String feature3) {
        this.feature3 = feature3 == null ? null : feature3.trim();
    }

    public String getFeature4() {
        return feature4;
    }

    public void setFeature4(String feature4) {
        this.feature4 = feature4 == null ? null : feature4.trim();
    }

    public String getFeature5() {
        return feature5;
    }

    public void setFeature5(String feature5) {
        this.feature5 = feature5 == null ? null : feature5.trim();
    }

    public String getFeature6() {
        return feature6;
    }

    public void setFeature6(String feature6) {
        this.feature6 = feature6 == null ? null : feature6.trim();
    }

    public String getFeature7() {
        return feature7;
    }

    public void setFeature7(String feature7) {
        this.feature7 = feature7 == null ? null : feature7.trim();
    }

    public String getFeature8() {
        return feature8;
    }

    public void setFeature8(String feature8) {
        this.feature8 = feature8 == null ? null : feature8.trim();
    }

    public String getFeature9() {
        return feature9;
    }

    public void setFeature9(String feature9) {
        this.feature9 = feature9 == null ? null : feature9.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    /**
     * 添加flag标记
     */
    public void addFlag(ShopPageFlagConstants f) {
        if (null == f) {
            return;
        }
        if (null == flag) {
            flag = Long.valueOf(0);
        }
        flag = flag | f.getValue();
    }

    /**
     * 删除flag标记
     */
    public void removeFlag(ShopPageFlagConstants f) {
        if (null == f) {
            return;
        }
        if (null == flag) {
            flag = Long.valueOf(0);
        }
        flag = flag & ~f.getValue();
    }

    /**
     *判断是否存在某个标志
     */
    public boolean isExistFlag(ShopPageFlagConstants f){

        if(flag == null || flag ==0){
            return false;
        }
        long support = f.getValue() & flag;
        return support > 0;
    }

}