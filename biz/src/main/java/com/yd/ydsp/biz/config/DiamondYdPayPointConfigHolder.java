package com.yd.ydsp.biz.config;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.common.lang.StringUtil;

import java.util.Map;

/**
 * @author zengyixun
 * @date 17/11/15
 */
public class DiamondYdPayPointConfigHolder {
    static final DiamondYdPayPointConfigHolder instance = new DiamondYdPayPointConfigHolder();
    public static DiamondYdPayPointConfigHolder getInstance() {
        return instance;
    }

    public String freeV="{\"setMealType\":\"freeV\",\"name\":\"体验版\",\"price\":0,\"y1\":0.9,\"y2\":0.8,\"y3\":0.6,\"orderCountToday\":10,\"desc\":\"日单量10<br/>加油包:8元/50单\",\"minMonth\":12,\"contractId\":100008901,\"bagPrice\":1,\"bagOrderNum\":50,\"id\":1}";
    public String basicV="{\"setMealType\":\"basicV\",\"name\":\"基础版\",\"price\":1,\"y1\":0.9,\"y2\":0.8,\"y3\":0.6,\"orderCountToday\":100,\"desc\":\"日单量100<br/>加油包:10元/100单\",\"minMonth\":12,\"contractId\":100008901,\"bagPrice\":1000,\"bagOrderNum\":100,\"id\":2}";
    public String professionalV="{\"setMealType\":\"professionalV\",\"name\":\"专业版\",\"price\":8000,\"y1\":0.9,\"y2\":0.8,\"y3\":0.6,\"orderCountToday\":300,\"desc\":\"日单量300<br/>加油包:10元/100单\",\"minMonth\":12,\"contractId\":100008901,\"bagPrice\":1000,\"bagOrderNum\":100,\"id\":3}";
    public String goldV="{\"setMealType\":\"goldV\",\"name\":\"黄金版\",\"price\":18000,\"y1\":0.9,\"y2\":0.8,\"y3\":0.6,\"orderCountToday\":900,\"desc\":\"日单量900<br/>加油包:10元/100单\",\"minMonth\":12,\"contractId\":100008901,\"bagPrice\":1000,\"bagOrderNum\":100,\"id\":4}";
    public String diamondV="{\"setMealType\":\"diamondV\",\"name\":\"钻石版\",\"price\":36000,\"y1\":0.9,\"y2\":0.8,\"y3\":0.5,\"orderCountToday\":9999999,\"desc\":\"日单量不限\",\"minMonth\":12,\"contractId\":100008901,\"bagPrice\":0,\"bagOrderNum\":0,\"id\":5}";


    public String getFreeV(){ return freeV; }
    public void setFreeV(String freeV){ this.freeV = freeV; }

    public String getBasicV(){ return basicV; }
    public void setBasicV(String basicV){ this.basicV = basicV; }

    public String getProfessionalV(){ return professionalV; }
    public void setProfessionalV(String professionalV){ this.professionalV = professionalV; }

    public String getGoldV(){ return goldV; }
    public void setGoldV(String goldV){ this.goldV = goldV; }

    public String getDiamondV(){ return diamondV; }
    public void setDiamondV(String diamondV){ this.diamondV = diamondV; }

    public Integer getOrderCountToday(String setMealTypeVersion){
        Map setMeal = this.getSetMealObject(setMealTypeVersion);

        if(setMeal==null){
            return 0;
        }

        return (Integer)setMeal.get("orderCountToday");

    }

    public Map getSetMealObject(String setMealTypeVersion){
        if(StringUtil.isBlank(setMealTypeVersion)){
            return null;
        }
        setMealTypeVersion = setMealTypeVersion.trim().toLowerCase();
        String versionType = null;
        if(setMealTypeVersion.equals("freeV".toLowerCase())){
            versionType = this.getFreeV();
        }
        if(setMealTypeVersion.equals("basicV".toLowerCase())){
            versionType = this.getBasicV();
        }
        if(setMealTypeVersion.equals("professionalV".toLowerCase())){
            versionType = this.getProfessionalV();
        }
        if(setMealTypeVersion.equals("goldV".toLowerCase())){
            versionType = this.getGoldV();
        }
        if(setMealTypeVersion.equals("diamondV".toLowerCase())){
            versionType = this.getDiamondV();
        }
        if(StringUtil.isNotBlank(versionType)){
            Map<String,Object> result = JSON.parseObject(versionType,Map.class);
            return result;
        }
        return null;
    }

}
