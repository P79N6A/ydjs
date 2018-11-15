package com.yd.ydsp.client.domian.openshop.components;

import java.io.Serializable;

public class ComponentVO<T> implements Serializable,Comparable<ComponentVO>{

    /**
     * 组件排序
     */
    Integer sn;

    String nameStr;

    /**
     * 组件的类型
     * 0-banner; 1-资源组件; 2-图片组件; 3-点播组件; 4-直播;  5-组合展示区；6-商品组合展示区组件; 7-新品上市展示区组件；8-商家推荐展示区；9-热销商品展示区；
     */
    Integer componentType;

    /**
     * 范性对象，可能是componentType中的任何一种对象Map,banner则为List<ResourceInfoVO>,资源组件则为ResourceInfoVO,组合展示区则为CustomShowItemVO
     */
    private T component;

    public static <T> ComponentVO<T> createComponent(T t) {
        ComponentVO<T> componentVO = new ComponentVO<T>();
        componentVO.setComponent(t);
        return componentVO;
    }

    public static <T> ComponentVO<T> createComponent(T t, Integer sn) {
        ComponentVO<T> componentVO = new ComponentVO<T>();
        componentVO.setComponent(t);
        componentVO.setSn(sn);
        return componentVO;
    }

    public static <T> ComponentVO<T> createComponent(T t, Integer sn, String objName) {
        ComponentVO<T> componentVO = new ComponentVO<T>();
        componentVO.setComponent(t);
        componentVO.setSn(sn);
        componentVO.setNameStr(objName);
        return componentVO;
    }

    public Integer getSn() {
        return sn;
    }

    public void setSn(Integer sn) {
        this.sn = sn;
    }

    public String getNameStr() {
        return nameStr;
    }

    public void setNameStr(String nameStr) {
        this.nameStr = nameStr;
    }

    public T getComponent() {
        return component;
    }

    public void setComponent(T component) {
        this.component = component;
    }

    public Integer getComponentType() {
        return componentType;
    }

    public void setComponentType(Integer componentType) {
        this.componentType = componentType;
    }

    @Override
    public int compareTo(ComponentVO o) {
        return this.getSn().intValue() - o.getSn().intValue();
    }
}
