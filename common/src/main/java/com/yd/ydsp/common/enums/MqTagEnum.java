package com.yd.ydsp.common.enums;

/**
 * Created by zengyixun on 17/9/4.
 */
public enum MqTagEnum {

    YDPAY("ydpay"),//支付成功tag
    MicroPay("micropay"),//收钱包支付后发消息进行订单状态查询及订单状态更新操作
    ORDERTIMEOUT2C("ordertimeout2c"),//C端订单超时
    ORDERTIMEOUTCP("ordertimeoutcp"),//CP向引灯购货的订单超时
    WEIXINOPENMSG("weixinopenmsg"),//微信第三方开发平台消息
    WEIXINUSERMSG("weixinusermsg"),//微信用户消息，需要异步进行处理时
    WEIXINAUTHSHOP("weixinauthshop"),//微信公众号或者小程序授权成功给我们后，需要创建店铺的消息
    WEIXINSMALLCODE("weixinsamllcode"),//为已经受权的小程序上传代码
    ORDERDELIVERY("orderdelivery"),//物流订单状态变更
    USERORDERNEW("userordernew"),//同步用户订单到opensearch
    USERORDERUPDATE("userorderupdate"),//同步用户订单到opensearch
    USERORDERTIMEOUT("userordertimeout"),//同步用户订单到opensearch并关闭订单
    PRINTMSG("printmsg"),//订单打印消息
    WAREUPDATE("wareupdate"),//商品信息更新处理
    WARESELL("waresell"),//商品售卖数与库存更新消息
    SHOPUSERBILL("shopuserbill"),//账单变动消息
    UPDATESHOPCASHRECHARGE("updateshopcashrecharge"),//更新店铺充值表的统计数据信息的消息
    ;

    public String tag;

    MqTagEnum(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public static MqTagEnum tagOf(String tag) {

        for (MqTagEnum compareEnum : MqTagEnum.values()) {
            if (compareEnum.getTag().equals(tag)) {
                return compareEnum;
            }

        }
        return null;
    }

}
