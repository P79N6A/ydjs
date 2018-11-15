package com.yd.ydsp.client.domian.openshop;

import com.yd.ydsp.common.utils.FeatureUtil;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class YdCpMemberCardDTO {

    private String shopid;

    private String cardid;

    private String levelName;

    private Integer levelType;

    private Integer minPoint;

    private Integer maxPoint;

    private Integer deductionPoint;

    private Integer deductionMoney;

    private Integer accPoint;

    private Integer accMoney;

    private BigDecimal rechargeCardMaxMoney;

    /**
     * 持卡用户数
     */
    private Long userCount;

    /**
     * 充值用户数
     */
    private Long payUserCount;

    private String feature;
    protected Map<String, String> featureMap;

    private Integer status;


    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid == null ? null : shopid.trim();
    }

    public String getCardid() {
        return cardid;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid == null ? null : cardid.trim();
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName == null ? null : levelName.trim();
    }

    public Integer getLevelType() {
        return levelType;
    }

    public void setLevelType(Integer levelType) {
        this.levelType = levelType;
    }

    public Integer getMinPoint() {
        return minPoint;
    }

    public void setMinPoint(Integer minPoint) {
        this.minPoint = minPoint;
    }

    public Integer getMaxPoint() {
        return maxPoint;
    }

    public void setMaxPoint(Integer maxPoint) {
        this.maxPoint = maxPoint;
    }

    public Integer getDeductionPoint() {
        return deductionPoint;
    }

    public void setDeductionPoint(Integer deductionPoint) {
        this.deductionPoint = deductionPoint;
    }

    public Integer getDeductionMoney() {
        return deductionMoney;
    }

    public void setDeductionMoney(Integer deductionMoney) {
        this.deductionMoney = deductionMoney;
    }

    public Integer getAccPoint() {
        return accPoint;
    }

    public void setAccPoint(Integer accPoint) {
        this.accPoint = accPoint;
    }

    public Integer getAccMoney() {
        return accMoney;
    }

    public void setAccMoney(Integer accMoney) {
        this.accMoney = accMoney;
    }

    public BigDecimal getRechargeCardMaxMoney() {
        return rechargeCardMaxMoney;
    }

    public void setRechargeCardMaxMoney(BigDecimal rechargeCardMaxMoney) {
        this.rechargeCardMaxMoney = rechargeCardMaxMoney;
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

    public Long getUserCount() {
        return userCount;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }

    public Long getPayUserCount() {
        return payUserCount;
    }

    public void setPayUserCount(Long payUserCount) {
        this.payUserCount = payUserCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}