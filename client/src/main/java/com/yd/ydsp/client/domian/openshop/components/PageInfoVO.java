package com.yd.ydsp.client.domian.openshop.components;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.*;

public class PageInfoVO implements Serializable {

    /**
     * 页面名称
     */
    String pageName;
    /**
     * 页面ID
     */
    String pageId;

    /**
     * 店铺id
     */
    String shopid;

    /**
     * 页面类型
     */
    Integer pageType=0;
    /**
     * 状态:0-已经发布；1-存在没有发布的版本
     */
    Integer Status=1;

    /**
     * 背景色，可以为空，为空则系统使用自己的默认值
     */
    String backColor="#ffffff";

    /**
     * 页面布局的组件列表
     */
    List<ComponentVO> componentList;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifyDate;

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public Integer getPageType() {
        return pageType;
    }

    public void setPageType(Integer pageType) {
        this.pageType = pageType;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    public List<ComponentVO> getComponentList() {
        return componentList;
    }

    public void setComponentList(List<ComponentVO> componentList) {
        this.componentList = componentList;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public static void main(String[] args) throws Exception {

//        ResourceInfoVO resourceInfoVO = new ResourceInfoVO();
//        resourceInfoVO.setResourceName("测试1");
//        List<ResourceInfoVO> voList = new ArrayList<ResourceInfoVO>();
//        voList.add(resourceInfoVO);
//
//        ComponentVO componentVO = new ComponentVO();
//        componentVO.setSn(1);
//        componentVO.setComponent(voList);
//
//        List<ComponentVO> list = new ArrayList<ComponentVO>();
//        list.add(componentVO);
//
//        PageInfoVO pageInfoVO = new PageInfoVO();
//        pageInfoVO.setBackColor("#898788");
//        pageInfoVO.setComponentList(list);
//        pageInfoVO.setPageName("测试页");
//
//        String jsonStr = JSON.toJSONString(pageInfoVO);
//        PageInfoVO pageInfoVO1 = JSON.parseObject(jsonStr,PageInfoVO.class);
//
//        System.out.println(JSON.toJSONString(pageInfoVO1));

        // TODO Auto-generated method stub
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("name", "p");
        map1.put("cj", 5);
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("name", "h");
        map2.put("cj", 12);
        Map<String, Object> map3 = new HashMap<String, Object>();
        map3.put("name", "f");
        map3.put("cj", 31);
        list.add(map1);
        list.add(map3);
        list.add(map2);
        //排序前
        for (Map<String, Object> map : list) {
            System.out.println(map.get("cj"));
        }
        Collections.sort(list, new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Integer name1 = Integer.valueOf(o1.get("cj").toString()) ;//name1是从你list里面拿出来的一个
                Integer name2 = Integer.valueOf(o2.get("cj").toString()) ; //name1是从你list里面拿出来的第二个name
                return name1.compareTo(name2);
            }
        });

        for (Map map:list){
            if(((Integer)map.get("cj")).intValue()==12){
                map.put("name","zyx");
            }
        }

        for (Map map:list){
            System.out.println(map.get("name"));
        }

    }
}
