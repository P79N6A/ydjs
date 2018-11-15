package com.yd.ydsp.biz.weixin.model;

/**
 * Created by zengyixun on 17/9/4.
 */
public enum WeixinHeadImgTypeEnum {

    NOTHING("nothing", 0),//无头像
    WEIXINURL("weixinurl", 1),//微信url地址头像
    OSSURL("ossurl", 2),//已经上传到oss的头像
    LOCAL("local",3);//保存在本地的头像

    public String name;

    public Integer type;

    WeixinHeadImgTypeEnum(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public static Integer getTypeByName(String name) {
        Integer type = null;
        for (WeixinHeadImgTypeEnum compareEnum : WeixinHeadImgTypeEnum.values()) {
            if (compareEnum.getName().equals(name)) {
                type = compareEnum.getType();
            }
        }
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }


}
