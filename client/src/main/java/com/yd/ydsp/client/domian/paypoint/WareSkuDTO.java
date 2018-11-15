package com.yd.ydsp.client.domian.paypoint;

import com.yd.ydsp.common.constants.paypoint.WareSkuFlagConstants;
import com.yd.ydsp.common.utils.FeatureUtil;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author zengyixun
 * @date 18/1/8
 */
public class WareSkuDTO implements Serializable {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 店铺id
     */
    private String shopid;

    /**
     * 商品id
     */
    private String skuid;

    /**
     * 商品类目id
     */
    private String wareitemid;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 图片url
     */
    private String wareimg;

    /**
     * 商品描述信息
     */
    private String description;

    /**
     *属性标签
     * 0-无类型，1-火(推荐），2-特(特价），4-折(打折），计算方法:0无类型，1火，2特，3－火＋特，4折，5-火＋折，6-特+折,7-又火又特又折'
     */
    private Integer waretype=0;

    /**
     * 打标字段
     */
    private Long flag=0L;

    /**
     * 已经卖出数量，不再只表示一个月的数量了
     */
    private Integer sellCountMonth=0;

    /**
     * 商品排序
     */
    private Integer wareseq;

    /**
     * 库存，目前可以使用1表示在售，0表示停售
     */
    private Integer inventory=99999;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 打折价格
     */
    private BigDecimal disprice;

    /**
     * 状态：0使用中，1已经删除
     */
    private Integer status;

    private String feature;
    protected Map<String, String> featureMap;


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

    public String getSkuid(){ return skuid; }

    public void setSkuid(String skuid){
        this.skuid = skuid == null ? null : skuid.trim();
    }

    public String getWareitemid() {
        return wareitemid;
    }

    public void setWareitemid(String wareitemid) {
        this.wareitemid = wareitemid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getWareimg() {
        return wareimg;
    }

    public void setWareimg(String wareimg) {
        this.wareimg = wareimg == null ? null : wareimg.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Integer getWaretype() {
        return waretype;
    }

    public void setWaretype(Integer waretype) {
        this.waretype = waretype;
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
    public void addFlag(WareSkuFlagConstants f) {
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
    public void removeFlag(WareSkuFlagConstants f) {
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
    public boolean isExistFlag(WareSkuFlagConstants f){

        if(flag == null || flag ==0){
            return false;
        }
        long support = f.getValue() & flag;
        return support > 0;
    }

    public Integer getSellCountMonth() {
        return sellCountMonth;
    }

    public void setSellCountMonth(Integer sellCountMonth) {
        this.sellCountMonth = sellCountMonth;
    }

    public Integer getWareseq() {
        return wareseq;
    }

    public void setWareseq(Integer wareseq) {
        this.wareseq = wareseq;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDisprice() {
        return disprice;
    }

    public void setDisprice(BigDecimal disprice) {
        this.disprice = disprice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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


}
