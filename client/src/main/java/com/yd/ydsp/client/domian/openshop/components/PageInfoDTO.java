package com.yd.ydsp.client.domian.openshop.components;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.common.constants.paypoint.ShopPageFlagConstants;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.FeatureUtil;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class PageInfoDTO {
    private Long id;

    /**
     * 店铺id
     */
    private String shopid;

    /**
     * 页面id
     */
    private String pageid;

    /**
     * 页面名称
     */
    private String pageName;

    /**
     * 页面类型
     */
    private Integer pageType=0;

    private List<ComponentVO> componentList;
    /**
     * 组件JSONLIST字串
     */
    private String components;

    private Long flag=0L;

    private String feature;
    protected Map<String, String> featureMap;

    private Integer status=1;

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

    public String getPageid() {
        return pageid;
    }

    public void setPageid(String pageid) {
        this.pageid = pageid == null ? null : pageid.trim();
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName == null ? null : pageName.trim();
    }

    public Integer getPageType() {
        return pageType;
    }

    public void setPageType(Integer pageType) {
        this.pageType = pageType;
    }

    public String getComponents() {
        return components;
    }

    public void setComponents(String components) {
        this.components = components == null ? null : components.trim();
        if(StringUtil.isNotEmpty(this.components)){
            this.componentList = JSON.parseArray(this.components,ComponentVO.class);
        }
    }

    public Long getFlag() {
        return flag;
    }

    public void setFlag(Long flag) {
        this.flag = flag;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public List<ComponentVO> getComponentList() {
        return componentList;
    }

    public void setComponentList(List<ComponentVO> componentList) {
        this.componentList = componentList;
    }
}