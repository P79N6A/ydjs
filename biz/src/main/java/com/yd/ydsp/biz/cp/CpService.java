package com.yd.ydsp.biz.cp;

import com.yd.ydsp.biz.cp.model.ShopMainPageVO;
import com.yd.ydsp.biz.customer.model.Order2CVO;
import com.yd.ydsp.biz.customer.model.order.ShopOrder2C;
import com.yd.ydsp.biz.customer.model.order.ShopOrderExt2CP;
import com.yd.ydsp.client.domian.ContractInfoVO;
import com.yd.ydsp.client.domian.UserContractVO;
import com.yd.ydsp.client.domian.openshop.SearchOrderDataResultVO;
import com.yd.ydsp.client.domian.paypoint.BankInfoVO;
import com.yd.ydsp.client.domian.paypoint.DiningtableVO;
import com.yd.ydsp.common.enums.PayOrderTypeEnum;
import com.yd.ydsp.common.enums.SourceEnum;
import com.yd.ydsp.common.enums.paypoint.PrintTypeEnum;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.dal.entity.YdTableQrcode;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author zengyixun
 * @date 17/9/17
 */
public interface CpService {

    /**
     * 增加设备到店铺
     * @param openid
     * @param shopid
     * @param sn
     * @param deviceType
     * @param description
     * @return
     */
    String addDevice(String openid, String shopid, String name, String sn, TypeEnum deviceType,String description);

    /**
     * 增加打印机
     * @param openid
     * @param shopid
     * @param sn
     * @param times 打印次数
     * @param typeEnum
     * @param description
     * @return
     */
    String addPrinter(String openid, String shopid, String name, String sn,String key, PrintTypeEnum typeEnum, Integer times, String description);

    /**
     * 修改设备信息
     * @param openid
     * @param deviceid
     * @param shopid
     * @param sn
     * @param description
     * @return
     */
    boolean modifyDevice(String openid,String deviceid, String shopid, String name, String sn, TypeEnum deviceType,String description);

    /**
     * 修改打印机信息
     * @param openid
     * @param deviceid
     * @param shopid
     * @param sn
     * @param times 打印次数
     * @param description
     * @return
     */
    boolean modifyPrinter(String openid,String deviceid, String shopid, String name, String sn,String key, Integer times,PrintTypeEnum typeEnum,String description);

    /**
     * 删除设备信息
     * @param openid
     * @param shopid
     * @param deviceid
     * @return
     */
    boolean delDevice(String openid,String shopid,String deviceid);

    /**
     * 批量绑定桌位打印机
     * @param openid
     * @param shopid
     * @param printerid
     * @param tables
     * @return
     */
    Map<String,Object> batchBindPrinter(String openid,String shopid,String printerid,List<String> tables);

    /**
     * 根据id查设备信息
     * @param openid
     * @param shopid
     * @param deviceid
     * @return
     */
    Object queryDeviceInfo(String openid,String shopid,String deviceid,TypeEnum type);
    Object queryDeviceInfo(String shopid,String deviceid,TypeEnum type);

    /**
     * 查一个店下某种设备的所有内容
     * @param openid
     * @param shopid
     * @return
     */
    List<Object> queryDeviceInfoList(String openid, String shopid,TypeEnum type,PrintTypeEnum printTypeEnum);
    List<Object> queryDeviceInfoList(String openid, String shopid, TypeEnum type, PrintTypeEnum printTypeEnum, SourceEnum sourceEnum);

    /**
     * 增加一个桌位
     * @param openid
     * @param diningtableVO
     * @return
     */
    String addTable(String openid, DiningtableVO diningtableVO) throws Exception;

    /**
     * 修改一个桌位
     * @param openid
     * @param diningtableVO
     * @return
     */
    boolean modifyTable(String openid, DiningtableVO diningtableVO) throws Exception;
    boolean modifyQrcode(String openid, List<YdTableQrcode> qrcodes);

    /**
     * 删除一个桌位
     * @param openid
     * @param shopid
     * @param tableid
     * @return
     */
    boolean delTable(String openid , String shopid, String tableid);

    /**
     * 根据桌位id查询一个座位
     * @param shopid
     * @param tableid
     * @return
     */
    DiningtableVO queryTableByTableId(String shopid, String tableid);

    /**
     * 根据扫码码查询一个座位
     * @param qrcode
     * @return
     */
    DiningtableVO queryTableByQrcode(String qrcode);

    List<YdTableQrcode> getQrcodes(String openid,String tableid);

    /**
     * 根据shopid查询桌位
     * @param openid
     * @param shopid
     * @param pageIndex 从1开始
     * @param count 每页数量
     * @return
     * select * from persons limit 5 offset 0 ;

            意思是，起点0开始查询，返回5条数据。
    select * from persons limit 5 offset 5 ;

    意思是，起点5开始查询，返回5条数据。
     */
    List<DiningtableVO> queryTableByShopId(String openid , String shopid, Integer pageIndex, Integer count);

    /**
     * 取一个Qrcode的随机值
     * @param openid
     * @param shopid
     * @return
     */
    String getRandomQrCode(String openid, String shopid,Integer codeType);


    /**
     * 取合约的名称与url地址
     * @param contractId
     * @return
     */
    ContractInfoVO getContractInfo(String contractId);

    /**
     * 签约查询页显示列表内容
     * @param openid
     * @param shopid
     * @return
     */
    List<UserContractVO> getUserContractInfo(String openid,String shopid);

    /**
     * 取店铺主页今日实时数据信息
     * @param openid
     * @param shopid
     * @return
     */
    ShopMainPageVO getShopMainPageData(String openid,String shopid);

    /**
     * 订单管理页中的订单查询接口
     * @param openid
     * @param shopid
     * @param queryDate
     * @param pageIndex 从1开始
     * @param count 每页数量
     * @param tableid
     * @return
     */
    List<Order2CVO> getOrder2CByShop(String openid, String shopid, Date queryDate, String tableid,Integer pageIndex, Integer count);

    /**
     * 查订单详情
     * @param openid
     * @param shopid
     * @param orderid
     * @return
     */
    Order2CVO getOrder2CDetailByShop(String openid,String shopid, String orderid);

    /**
     *计算修改后的订单金额
     * @param openid
     * @param order2CVO
     * @return
     */
    Order2CVO calculateOrder2C(String openid ,Order2CVO order2CVO);

    /**
     * 提交修改订单
     * @param openid
     * @param order2CVO
     * @return
     */
    boolean modifyOrder2C(String openid, Order2CVO order2CVO);

    /**
     * 管理员取消订单
     * @param openid
     * @param orderid
     * @return
     */
    boolean cancelOrder2C(String openid,String shopid, String orderid);


    /**
     * 管理员或者服务员确认消费者已经使用现金支付了订单金额
     * @param openid
     * @param shopid
     * @param orderid
     * @return
     */
    boolean useCashPayWithOrder2C(String openid,String shopid, String orderid);

    /**
     * 取银行卡信息，必须严格按openid shopid取得
     * @param openid
     * @param shopid
     * @return
     */
    BankInfoVO getBankInfo(String openid, String shopid);

    /**
     * 一个店铺的owner进行银行收款帐号信息提交
     * @param openid
     * @param bankInfoVO
     * @return
     */
    boolean saveBankInfo(String openid,BankInfoVO bankInfoVO);

    /**
     * ------------------------------------独立的商城模式-------------------------------------------
     */

    /**
     * 独立商城模式下计算修改后的订单金额
     * @param openid
     * @param shopOrder2C
     * @return
     */
    ShopOrder2C calculateUserOrder2C(String openid, ShopOrder2C shopOrder2C);


    /**
     * 商家修改独立商城模式下的用户订单
     * @param openid
     * @param shopOrder2C
     * @return
     */
    boolean modifyUserOrder2C(String openid, ShopOrder2C shopOrder2C);

    /**
     * 商家取消独立模式下的用户订单
     * @param openid
     * @param shopid
     * @param orderid
     * @return
     */
    boolean cancelUserOrder2C(String openid,String shopid, String orderid);

    /**
     * 商家确认订单已经现金支付
     * @param openid
     * @param shopid
     * @param orderid
     * @return
     */
    boolean offlineToPay(String openid,String shopid, String orderid);

    /**
     * 商家接单
     * @param openid
     * @param shopid
     * @param orderid
     * @return
     */
    boolean userOrderTaking(String openid, String shopid, String orderid);

    /**
     * 商家确认发货
     * @param openid
     * @param shopid
     * @param orderid
     * @param desc  商家填写运单号等信息
     * @return
     */
    boolean userOrderSendOut(String openid,String shopid,String orderid,String desc);

    /**
     * 查询堂食订单
     * @param openid
     * @param shopid
     * @param queryDate
     * @param status
     * @param pageIndex
     * @param count
     * @return
     */
    List<SearchOrderDataResultVO> queryShopOrderIndoor(String openid, String shopid,Date queryDate, String tableid,String status, Integer pageIndex, Integer count);

    /**
     * 查询收钱宝订单
     * @param openid
     * @param shopid
     * @param queryDate
     * @param status
     * @param pageIndex
     * @param count
     * @return
     */
    List<SearchOrderDataResultVO> queryShopAllMoneyOrder(String openid, String shopid, Date queryDate, String status, Integer pageIndex, Integer count);

    /**
     * 查询线上商城订单
     * @param openid
     * @param shopid
     * @param queryDate
     * @param channelid
     * @param status
     * @param pageIndex
     * @param count
     * @return
     */
    List<SearchOrderDataResultVO> queryShopOrderOnline(String openid, String shopid,Date queryDate, String channelid,String status, Integer pageIndex, Integer count);

    List<SearchOrderDataResultVO> queryShopAllOrder(String openid, String shopid,Date queryDate, String entercode,String status, Integer pageIndex, Integer count);

    /**
     * 根据orderid查询订单详情
     * @param openid
     * @param orderid
     * @return
     */
    ShopOrderExt2CP queryUserOrderDetail(String openid, String shopid, String orderid);


    /**
     * 创建收钱宝订单
     * @param openid
     * @param money
     * @param desc
     * @return 订单id
     */
    String createMoneyOrder(String openid, String shopid, BigDecimal money, String name, String desc);

    /**
     * 商家扫消费者的付款码收钱
     * @param openid
     * @param orderid
     * @param authCode
     * @return
     */
    Map<String,Object> microPay(String openid,String orderid,String authCode) throws Exception;

    /**
     * 查询微信支付订单的结果
     * @param openid
     * @param orderid
     * @return
     */
    Map<String,Object> queryWeiXinPayOrder(String openid,String orderid) throws Exception;

}
