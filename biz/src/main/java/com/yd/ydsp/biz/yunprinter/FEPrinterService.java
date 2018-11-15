package com.yd.ydsp.biz.yunprinter;

import java.util.List;
import java.util.Map;

/**
 * Created by zengyixun on 17/9/25.
 */
public interface FEPrinterService {

        /**
         * 打印之前必须添加SN、KEY到开发者账户（查看打印机身标签）开发者可以在飞鹅管理后台页面人工添加，或者通过本接口程序添加
         * 打印机编号(必填) # 打印机识别码(必填) # 备注名称(选填) # 流量卡号码(选填)
         * 一次最多100台
         * @param printInfo
         * @return
         */
        Map<String,Object> printerAddlist(List<String> printInfo);

        /**
         * @param sn 打印机编号
         * @param orderMsg 打印的内容
         * @param times 打印次数，默认为1
         * @return 正确返回订单ID（外部订单号）
         */
        String printMsg(String sn,String orderMsg,String times);

        /**
         * 查询打印订单状态是否成功
         * @param printOrderId 订单ID，由printMsg返回
         * @return
         */
        boolean queryPrintOrderState(String printOrderId);

}
