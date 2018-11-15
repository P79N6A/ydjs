package com.yd.ydsp.client.domian.monitor;

import com.yd.ydsp.common.utils.FeatureUtil;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zengyixun
 * @date 17/12/16
 */
public class CPMonitorDTO {

    /**
     * id
     */
    private Long id;

    /**
     * 店铺id
     */
    private String shopid;

    /**
     * 店铺名称
     */
    private String name;

    /**
     * 图片文件的oss目录前缀
     */
    private String ossPath;

    /**
     * 启动图片路径
     */
    private String initImageUrl;

    /**
     * 电话
     */
    private String tel;

    /**
     * 设备通道信息
     * {"115939494":[1],"115939088":[1]}
     */
    private String deviceSerials;

    /**
     * 设备通道的名称信息
     */
    private String deviceSerialnames;

    /**
     * 描述信息 list 单数为大字体，双数内容为描述文字，以^^^来切分为List<String>
     * "简介"^^^"小红花幼儿园创建于2002年12月，是由黔江区教委审批合格，区民政局注册合法的一所民办幼儿园，我园多次获得经教委评为平安校园，重庆市“平安校园”等各种奖项，我园在教职工的努力下荣获“二级普惠”幼儿园殊荣。我们秉承“一切为了孩子”的宗旨，拥有一支热爱幼儿教育事业、热爱幼儿、热忱为家长服务、社会服务，经验丰富的教师队伍和后勤保障人员，培育了一批又一批的孩子，在这里每个人脸上都洋溢着幸福，都是快乐的小主人。"^^^"招生信息"^^^"小红花幼儿园常年招收3-6岁适龄儿童，如果你也想加入我们，就赶紧拨打招生热线：023-85089229"^^^"查园长电话"^^^"13908278992"^^^"地址"^^^"黔江区城东街道河街169号"
     */
    private String shopDesc;

    /**
     * 状态字段
     */
    private Integer status;

    /**
     * 扩展字段，比如存放图片的名称信息
     */
    private String feature;
    protected Map<String, String> featureMap;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getOssPath() {
        return ossPath;
    }

    public void setOssPath(String ossPath) {
        this.ossPath = ossPath == null ? null : ossPath.trim();
    }

    public String getInitImageUrl() {
        return initImageUrl;
    }

    public void setInitImageUrl(String initImageUrl) {
        this.initImageUrl = initImageUrl == null ? null : initImageUrl.trim();
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel == null ? null : tel.trim();
    }

    public String getDeviceSerials() {
        return deviceSerials;
    }

    public void setDeviceSerials(String deviceSerials) {
        this.deviceSerials = deviceSerials == null ? null : deviceSerials.trim();
    }

    public String getDeviceSerialnames() {
        return deviceSerialnames;
    }

    public void setDeviceSerialnames(String deviceSerialnames) {
        this.deviceSerialnames = deviceSerialnames == null ? null : deviceSerialnames.trim();
    }

    public String getShopDesc() {
        return shopDesc;
    }

    public void setShopDesc(String shopDesc) {
        this.shopDesc = shopDesc == null ? null : shopDesc.trim();
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
     * value可能是一个列表，比如记录健康证文件名列表
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

}
