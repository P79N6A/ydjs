package com.yd.ydsp.client.domian.paypoint;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zengyixun
 * @date 18/1/8
 */
public class WareSkuVO extends WareSkuBaseVO implements Serializable {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 图片urlList
     */
    private List<WareSkuPicVO> wareimgList;

    /**
     * 此字段不使用
     */
    private Integer waretype=0;

    /**
     * 打标字段
     */
    private Long flag=0L;


    /**
     * 重量（单位公斤)
     */
    private BigDecimal weight;
    /**
     * 体积长宽高(BigDecimal*BigDecimal*BigDecimal)与体积单位(Integer):0-mm;1-cm;2-m
     * @return
     */

    private Map<String,Object> volume;


    private Integer createDateInt;

    private Integer modifyDateInt;

//    /**
//     * 要删除的参数id列表
//     */
//    private List<String> delParameterIds;

    /**
     * 要增加的参数对象列表
     */
    private List<YdCpParameterInfoVO> parameterInfoVOList;

    /**
     * 对一个商品进行具体的规格组合及价格设置
     */
    private List<YdCpSpecConfigInfoVO> specConfigInfoVOList;

    /**
     * 图文列表更新
     */
    private List<WareSkuPicVO> skuPicVOList;

//
//    public List<String> getDelParameterIds() {
//        return delParameterIds;
//    }
//
//    public void setDelParameterIds(List<String> delParameterIds) {
//        this.delParameterIds = delParameterIds;
//    }

    public List<WareSkuPicVO> getSkuPicVOList() {
        return skuPicVOList;
    }

    public void setSkuPicVOList(List<WareSkuPicVO> skuPicVOList) {
        this.skuPicVOList = skuPicVOList;
    }

    public List<YdCpParameterInfoVO> getParameterInfoVOList() {
        return parameterInfoVOList;
    }

    public void setParameterInfoVOList(List<YdCpParameterInfoVO> parameterInfoVOList) {
        this.parameterInfoVOList = parameterInfoVOList;
    }

    public List<YdCpSpecConfigInfoVO> getSpecConfigInfoVOList() {
        return specConfigInfoVOList;
    }

    public void setSpecConfigInfoVOList(List<YdCpSpecConfigInfoVO> specConfigInfoVOList) {
        this.specConfigInfoVOList = specConfigInfoVOList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public Map<String, Object> getVolume() {
        return volume;
    }

    public void setVolume(Map<String, Object> volume) {
        this.volume = volume;
    }

    public List<WareSkuPicVO> getWareimgList() {
        return wareimgList;
    }

    public void setWareimgList(List<WareSkuPicVO> wareimgList) {
        this.wareimgList = wareimgList;
    }

    public Integer getCreateDateInt() {
        return createDateInt;
    }

    public Integer getModifyDateInt() {
        return modifyDateInt;
    }

    public void setCreateDateInt(Integer createDateInt) {
        this.createDateInt = createDateInt;
    }

    public void setModifyDateInt(Integer modifyDateInt) {
        this.modifyDateInt = modifyDateInt;
    }
}
