package com.yd.ydsp.client.domian.paypoint;

import com.yd.ydsp.common.constants.paypoint.YdUserSupportFlagConstants;
import com.yd.ydsp.common.utils.FeatureUtil;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zengyixun on 17/8/19.
 */
public class CpUserInfoDTO {
    /**
     * 自增ID
     */
    private Long id;

    /**
     * 微信用户维一id
     */
    private String unionid;

    /**
     * 微信单一公众号内用户唯一id，cp公众号中的用户维一id
     */
    private String openid;

    /**
     * 微信昵称
     */
    private String nick;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 城市
     */
    private String city;

    /**
     * 省
     */
    private String province;

    /**
     * 国家
     */
    private String country;

    /**
     * 头像url
     */
    private String headImgUrl;

    /**
     * 头像url的位置类型，0－无头像，1-微信，2-OSS，3-本地
     */
    private Integer headImgType;

    /**
     * 微信分组id
     */
    private Integer wexGroupId;

    /**
     * 用户绑定的手机，需要进行验证码验证
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户设置的cp管理后台登录密码
     */
    private String password;

    /**
     * 元字符，给密码加密时，会选择元字符先对密码进行一次混淆
     */
    private String original;

    /**
     * 店铺id
     */
    private String shopid;

    /**
     * cp用户能力标签
     */
    private Long flag;

    /**
     * 用户状态
     */
    private Integer status;

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
     * 记录修改时间
     */
    private Date modifyDate;

    /**
     * 修改者
     */
    private String modifier;

    private String weixinAccessToken;

    private String weixinRefreshToken;

    private Date weixinTokenExpireIn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid == null ? null : unionid.trim();
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid == null ? null : openid.trim();
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick == null ? null : nick.trim();
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
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

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl == null ? null : headImgUrl.trim();
    }

    public Integer getHeadImgType() {
        return headImgType;
    }

    public void setHeadImgType(Integer headImgType) {
        this.headImgType = headImgType;
    }

    public Integer getWexGroupId() {
        return wexGroupId;
    }

    public void setWexGroupId(Integer wexGroupId) {
        this.wexGroupId = wexGroupId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original == null ? null : original.trim();
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid == null ? null : shopid.trim();
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
    public void addFlag(YdUserSupportFlagConstants f) {
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
    public void removeFlag(YdUserSupportFlagConstants f) {
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
    private boolean isExistFlag(YdUserSupportFlagConstants f){

        if(flag == null || flag ==0){
            return false;
        }
        long support = f.getValue() & flag;
        return support > 0;
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
     * value可能是一个列表，比如记录此用户拥有多个店铺
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

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier == null ? null : modifier.trim();
    }

    public String getWeixinAccessToken() {
        return weixinAccessToken;
    }

    public void setWeixinAccessToken(String weixinAccessToken) {
        this.weixinAccessToken = weixinAccessToken == null ? null : weixinAccessToken.trim();
    }

    public String getWeixinRefreshToken() {
        return weixinRefreshToken;
    }

    public void setWeixinRefreshToken(String weixinRefreshToken) {
        this.weixinRefreshToken = weixinRefreshToken == null ? null : weixinRefreshToken.trim();
    }

    public Date getWeixinTokenExpireIn() {
        return weixinTokenExpireIn;
    }

    public void setWeixinTokenExpireIn(Date weixinTokenExpireIn) {
        this.weixinTokenExpireIn = weixinTokenExpireIn;
    }
}
