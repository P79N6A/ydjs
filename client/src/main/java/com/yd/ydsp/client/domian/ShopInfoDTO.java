package com.yd.ydsp.client.domian;

import com.yd.ydsp.common.constants.paypoint.ShopSupportFlagConstants;
import com.yd.ydsp.common.utils.FeatureUtil;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zengyixun on 17/8/18.
 */
public class ShopInfoDTO extends BaseDTO {

    /**
     * 自增值
     */
    private Long id;

    /**
     * 店铺id，自动生成一个15位的id
     */
    private String shopid;

    /**
     * 店铺图片，用于点餐页最上方的图片显示
     */
    private String shopImg;

    /**
     * 店铺名称，如果是同一家公司有多个店，需要区分出分店名称
     */
    private String name;

    /**
     * 公司营业执照代码，或者统一的15位社会信用代码
     */
    private String code;

    /**
     * 法人名字
     */
    private String corporation;

    /**
     * 联系人姓名
     */
    private String contact;

    /**
     * 店铺地址
     */
    private String address;

    /**
     * 邮编
     */
    private String zipcode;

    /**
     * 区县
     */
    private String district;

    /**
     * 城市
     */
    private String city;

    /**
     * 省份、自治区
     */
    private String province;

    /**
     * 国家
     */
    private String country;

    /**
     * 说明描述性文字
     */
    private String description;

    /**
     * 所属行业代码
     */
    private Integer trade;

    /**
     * 法人或者联系人手机
     */
    private String mobile;

    /**
     * 座机
     */
    private String telephone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 支付宝帐号，一期暂时不用
     */
    private String alipayAccount;

    /**
     * 微信子商户号，在我们的微信支付中，要帮助用户注册或者绑定商户号成为我们的子商户号，用于资金结算及消费者直接付款的帐号
     */
    private String weixinpaySubMchId;

    /**
     *支付帐号的实名，可以不传的
     */
    private String payeeRealName;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 打标字段
     */
    private Long flag;

    /**
     * 第一次加入时间
     */
    private Date jointime;

    /**
     * 合约开始时间，每年续约用
     */
    private Date contractTimeBegin;

    /**
     * 合约结束时间，每年续约用，以判断服务是否应该继续提供
     */
    private Date contractTimeEnd;

    /**
     * 店铺的硬件设备信息
     */
    private String deviceInfo;
    protected Map<String, String> deviceInfoMap;

    /**
     * 主页配置页面id
     */
    private String indexPageId;
    /**
     * 扩展字段
     */
    private String feature;
    protected Map<String, String> featureMap;

    /**
     * 记录创建时间
     */
    private Date createDate;

    /**
     * 记录最后一次修改时间
     */
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

    public String getShopImg() {
        return shopImg;
    }

    public void setShopImg(String shopImg) {
        this.shopImg = shopImg == null ? null : shopImg.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getCorporation() {
        return corporation;
    }

    public void setCorporation(String corporation) {
        this.corporation = corporation == null ? null : corporation.trim();
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact == null ? null : contact.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode == null ? null : zipcode.trim();
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district == null ? null : district.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country == null ? null : country.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Integer getTrade() {
        return trade;
    }

    public void setTrade(Integer trade) {
        this.trade = trade;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone == null ? null : telephone.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getAlipayAccount() {
        return alipayAccount;
    }

    public void setAlipayAccount(String alipayAccount) {
        this.alipayAccount = alipayAccount == null ? null : alipayAccount.trim();
    }

    public String getWeixinpaySubMchId() {
        return weixinpaySubMchId;
    }

    public void setWeixinpaySubMchId(String weixinpaySubMchId) {
        this.weixinpaySubMchId = weixinpaySubMchId == null ? null : weixinpaySubMchId.trim();
    }

    public String getPayeeRealName() {
        return payeeRealName;
    }

    public void setPayeeRealName(String payeeRealName) {
        this.payeeRealName = payeeRealName == null ? null : payeeRealName.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getFlag() {
        return flag;
    }

    public void setFlag(Long flag) {
        this.flag = flag;
    }

    /**
     * 添加店铺flag标记
     */
    public void addFlag(ShopSupportFlagConstants f) {
        if (null == f) {
            return;
        }
        if (null == flag) {
            flag = Long.valueOf(0);
        }
        flag = flag | f.getValue();
    }

    /**
     * 删除店铺flag标记
     */
    public void removeFlag(ShopSupportFlagConstants f) {
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
    public boolean isExistFlag(ShopSupportFlagConstants f){

        if(flag == null || flag ==0){
            return false;
        }
        long support = f.getValue() & flag;
        return support > 0;
    }

    /**
     *判断是否是餐馆
     */

    public boolean isEatery(){ return isExistFlag(ShopSupportFlagConstants.EATERY);}
    /**
     *判断是否支持卖水果
     */
    public boolean isFruit(){ return isExistFlag(ShopSupportFlagConstants.FRUIT);}
    /**
     *判断是否安装监控
     */
    public boolean isMonitor(){ return isExistFlag(ShopSupportFlagConstants.MONITOR);}

    public boolean isPayNow(){ return  isExistFlag(ShopSupportFlagConstants.PAYNOW); }

    public boolean userPayNow(){ return  isExistFlag(ShopSupportFlagConstants.PAYNOW2C); }

    public Date getJointime() {
        return jointime;
    }

    public void setJointime(Date jointime) {
        this.jointime = jointime;
    }

    public Date getContractTimeBegin() {
        return contractTimeBegin;
    }

    public void setContractTimeBegin(Date contractTimeBegin) {
        this.contractTimeBegin = contractTimeBegin;
    }

    public Date getContractTimeEnd() {
        return contractTimeEnd;
    }

    public void setContractTimeEnd(Date contractTimeEnd) {
        this.contractTimeEnd = contractTimeEnd;
    }

    public void removeAllDeviceInfo() {
        this.deviceInfo = null;
        this.deviceInfoMap = null;
    }
    private void resetDeviceInfo() {
        this.deviceInfo = FeatureUtil.toString(deviceInfoMap);
    }
    private void initDeviceInfoMap() {
        if (null == deviceInfoMap) {
            deviceInfoMap = this.getDeviceInfoMap(deviceInfo);
        }
    }

    public Map<String, String> getDeviceInfoMap() {
        return getDeviceInfoMap(deviceInfo);
    }

    public void setDeviceInfoMap(Map<String, String> deviceInfoMap) {
        this.deviceInfoMap = deviceInfoMap;
    }

    private Map<String, String> getDeviceInfoMap(String deviceInfo) {
        return FeatureUtil.toMap(deviceInfo);

    }

    /**
     * deviceInfo都是列表
     * @param key
     * @param value
     */
    public void addDeviceValue(String key, String value) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value) && !"null".equals(value)) {
            List<String> list = getDeviceValue(key);
            if(list==null || list.size()==0){
                initDeviceInfoMap();
                deviceInfoMap.put(key, value);
                resetDeviceInfo();
                return;
            }
            if(list!=null && list.size()>0 && !list.contains(value)){

                list.add(value);
                initDeviceInfoMap();
                deviceInfoMap.put(key, FeatureUtil.listToString(list));
                resetDeviceInfo();
                return;
            }
        }
    }

    /**
     * @param key
     * @param value
     */
    public void delDeviceValue(String key, String value) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value) && !"null".equals(value)) {
            List<String> list = getDeviceValue(key);
            if(list==null || list.size()==0){
                return;
            }
            if(list!=null && list.size()>0 && list.contains(value)){

                list.remove(value);
                initDeviceInfoMap();
                deviceInfoMap.put(key, FeatureUtil.listToString(list));
                resetDeviceInfo();
                return;
            }
        }
    }


    /**
     * 根据一个deviceType字串,获取一个List类型的value
     *
     * @param key
     * @return
     */
    public List<String> getDeviceValue(String key) {
        initDeviceInfoMap();
        String value = deviceInfoMap.get(key);
        return value == null ? null : FeatureUtil.strToList(value);
    }

    public boolean removeDevice(String key) {
        initDeviceInfoMap();
        boolean flag = false;
        if (deviceInfoMap.containsKey(key)) {
            deviceInfoMap.remove(key);
            resetDeviceInfo();
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo == null ? null : deviceInfo.trim();
    }

    public String getIndexPageId() {
        return indexPageId;
    }

    public void setIndexPageId(String indexPageId) {
        this.indexPageId = indexPageId == null ? null : indexPageId.trim();
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
