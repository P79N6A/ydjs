package com.yd.ydsp.client.domian.weixin;

import com.yd.ydsp.common.utils.FeatureUtil;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class YdWeixinServiceConfigDTO implements Serializable {
    private static final long serialVersionUID = 983188846771659168L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 访问token
     */
    private String accessToken;

    /**
     * 刷新token
     */
    private String authorizerRefreshToken;

    /**
     * 访问Token过期时间
     */
    private Date expiresInDate;

    /**
     * 为每一个公众号或者小程序分配的统一编号，与调用网址前缀组合而成服务端消息接收地址
     */
    private String weixinConfigId;

    /**
     * 微信类型：0-开放平台授权公众号；1-开放平台授权小程序；2-服务号；3-小程序；4-订阅号
     */
    private Integer weixinType;

    private String appid;

    private String shopid;

    private String secret;

    /**
     * 要设置在微信开发配置页中的消息请求地址，用于按收微信或者用户向我们服务器发起的请求，前缀+weixinConfigId组合而成
     */
    private String requestUrl;

    private String msgToken;

    private String encodingAesKey;

    private Integer encodeType;

    /**
     * 使用一个URL就能订开公众号或者小程序
     */
    private String qrcodeUrl;

    /**
     * 微信支付商户号
     */
    private String weixinpaySubMchId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 扩展字段
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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken == null ? null : accessToken.trim();
    }

    public String getAuthorizerRefreshToken() {
        return authorizerRefreshToken;
    }

    public void setAuthorizerRefreshToken(String authorizerRefreshToken) {
        this.authorizerRefreshToken = authorizerRefreshToken == null ? null : authorizerRefreshToken.trim();
    }

    public Date getExpiresInDate() {
        return expiresInDate;
    }

    public void setExpiresInDate(Date expiresInDate) {
        this.expiresInDate = expiresInDate;
    }

    public String getWeixinConfigId() {
        return weixinConfigId;
    }

    public void setWeixinConfigId(String weixinConfigId) {
        this.weixinConfigId = weixinConfigId == null ? null : weixinConfigId.trim();
    }

    public Integer getWeixinType() {
        return weixinType;
    }

    public void setWeixinType(Integer weixinType) {
        this.weixinType = weixinType;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid == null ? null : appid.trim();
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid == null ? null : shopid.trim();
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret == null ? null : secret.trim();
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl == null ? null : requestUrl.trim();
    }

    public String getMsgToken() {
        return msgToken;
    }

    public void setMsgToken(String msgToken) {
        this.msgToken = msgToken == null ? null : msgToken.trim();
    }

    public String getEncodingAesKey() {
        return encodingAesKey;
    }

    public void setEncodingAesKey(String encodingAesKey) {
        this.encodingAesKey = encodingAesKey == null ? null : encodingAesKey.trim();
    }

    public Integer getEncodeType() {
        return encodeType;
    }

    public void setEncodeType(Integer encodeType) {
        this.encodeType = encodeType;
    }

    public String getQrcodeUrl() {
        return qrcodeUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl == null ? null : qrcodeUrl.trim();
    }

    public String getWeixinpaySubMchId() {
        return weixinpaySubMchId;
    }

    public void setWeixinpaySubMchId(String weixinpaySubMchId) {
        this.weixinpaySubMchId = weixinpaySubMchId == null ? null : weixinpaySubMchId.trim();
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
