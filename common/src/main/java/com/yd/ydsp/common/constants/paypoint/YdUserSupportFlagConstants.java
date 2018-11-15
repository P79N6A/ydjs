package com.yd.ydsp.common.constants.paypoint;

/**
 * Created by zengyixun on 17/8/18.
 */
public enum YdUserSupportFlagConstants {

    NEEDUPDATE(1 << 0, "需要更新用户微信信息"),
    YDWORKER(1 << 1, "引灯小二"),//在后台脚本订入此权限
    CP(1 << 2, "合作伙伴"),
    CONSUMER(1<<3,"引灯消费者"),
    AGENT(1<<6,"引灯加盟商"),
//    MANAGER(1 << 3, "管理员"),
//    WAITER(1 << 4, "餐厅服务员"),
    ;

    private Integer value;

    private String name;

    YdUserSupportFlagConstants(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
