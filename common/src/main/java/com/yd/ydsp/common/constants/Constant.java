package com.yd.ydsp.common.constants;

/**
 * Created by zengyixun on 17/9/13.
 * 所有常量的定义
 */

public class Constant {
    public static final String APPNAMECP="引灯智能店铺";//CP端应用名
    public static final String BIND_MOBILE = "bindMobile";
    public static final String BIND_MOBILE_CODE="bindMobileCode";
    public static final String BIND_MOBILE_ENDTIME="bindMobileEndTime";
    public static final String BIND_SHOP_MOBILE = "bindShopMobile";//申请店铺入驻时填写手机，一般是负责人（法人）手机
    public static final String BIND_SHOP_MOBILE_CODE="bindShopMobileCode";
    public static final String BIND_SHOP_MOBILE_ENDTIME="bindShopMobileEndTime";

    public static final String BIND_OWNER_MOBILE = "bindOwnerMobile";//负责人进行信息变更时验证手机
    public static final String BIND_OWNER_MOBILE_CODE="bindOwnerMobileCode";
    public static final String BIND_OWNER_MOBILE_ENDTIME="bindOwnerMobileEndTime";
    public static final String BIND_OWNER_NEW_MOBILE = "bindOwnerNewMobile";//新的负责人进行信息提交时验证手机
    public static final String BIND_OWNER_NEW_MOBILE_CODE="bindOwnerNewMobileCode";
    public static final String BIND_OWNER_NEW_MOBILE_ENDTIME="bindOwnerNewMobileEndTime";

    public static final String BIND_AGENT_MOBILE = "bindAgentMobile";//加盟商手机绑定
    public static final String BIND_AGENT_MOBILE_CODE = "bindAgentMobileCode";
    public static final String BIND_AGENT_MOBILE_ENDTIME = "bindAgentMobileEndTime";//加盟商手机绑定

    public static final String BIND_XIAOER_MOBILE = "bindXiaoerMobile";//小二手机绑定
    public static final String BIND_XIAOER_MOBILE_CODE = "bindXiaoerMobileCode";
    public static final String BIND_XIAOER_MOBILE_ENDTIME = "bindXiaoerMobileEndTime";//小二手机绑定

    public static final String YD_MANAGER_XIAOER="ydManagerXiaoer"; //引灯小二，超级权限
    public static final String AGENT_MANAGER="agentManager"; //引灯代理商管理员
    public static final String DEFAULT_AGENTID ="defaultAgentId";//默认服务商
    public static final String AGENTIDList ="agentIds";//服务商列表
    public static final String cityCodes = "cityCodes";//城市编码
    public static final String ownerAgent = "ownerAgent";//加盟商负责人
    public static final String sigCode = "sigCode";//代理商签约码
    public static final String CPNUM = "cpnum";//CP的数量

    public static final String CP_SHOPS_MANAGER="cpShopsManager"; //此CPUser能对服务员进行管理及操作相关功能，可能他是多个店铺的管理员
    public static final String CP_SHOPS_OWNER="cpShopsOwner";//店铺负责人，可能有多个店铺
    public static final String CP_SHOPS_WAITER="cpShopsWaiter";//服务员，可能在多个店铺进行服务
    public static final String CP_ORDER_TYPE="cpOrderType";//cp订单类型，value为套餐name
    public static final String CP_ORDER_AMOUNT="cpOrderAmount";//套餐价格
    public static final String SHOP_SETMEAL_TYPE="shopSetMealType";//店铺套餐类型
    public static final String IDENTITY_NUMBER ="identityNumber";//身份证号码
    public static final String DEFAULT_ADDRESS ="defaultAddress";//默认地址
    public static final String DEFAULT_SHOP ="defaultShop";//默认店铺
    public static final String FREE_VERSION="freeV";//体验版
    public static final String BASIC_VERSION="basicV";//基础版
    public static final String PROFESSIONAL_VAERSION="professionalV";//专业版
    public static final String GOLD_VERSION = "goldV";//黄金版
    public static final String DIAMOND_VERSION = "diamondV";//钻石版
    public static final String OPENID = "openid";//公众号用户id
    public static final String ORDERID = "orderid";//订单id
    public static final String ORDERTYPE = "orderType";//订单类型
    public static final String PAYORDERTYPE = "payOrderType";//支付订单类型
    public static final String STATUS = "status";//状态
    public static final String BUSINESS_LICENCE = "bLicence";//营业执照
    public static final String BUSINESS_LICENCE_IMAGE = "bLicenceImage";//营业执照图片
    public static final String HEALTH_IMAGE = "healthImage";//健康证图片
    public static final String SHOP_DAILY_IMAGE = "shopDailyImage";//店铺日常图
    public static final String SETMEAL_AMOUNT="setMealAmount";//套餐金额
    public static final String SETMEAL_BEGINDATE="setMealBeginDate";//套餐开始日期
    public static final String SETMEAL_ENDDATE="setMealEndDate";//套餐结束日期
    public static final String ORIG="orig";//原价
    public static final String DISCOUNT="discount";//折后价
    public static final String PAYURL="payUrl";//折后价
    public static final String CPADDRESS="cpAddress";//合作商地址
    public static final String CONSUMERADDRESS="consumerAddress";//合作商地址
    public static final String YDGOODS="goods";//自营商品
    public static final String BAGORDERNUM="bagOrderNum";//加油包购买单量
    public static final String BAGORDERAMOUNT="bagOrderAmount";//加油包金额
    public static final String SETMEALTYPE="setMealType";//套餐类型
    public static final String UPSETMEAL="upSetMeal";//套餐升级
    public static final String UPSETMEAL_AMOUNT="upSetMealAmount";//套餐升级金额
    public static final String UPSETMEAL_REMANENT="upSetMealRemanent";//套餐升级的天数
    public static final String ORDERCOUNTTODAY="orderCountToday";//套餐中设置的日单量
    public static final String PRINT_TYPE="printType";//打印类型
    public static final String PRINT_TIMES="printTimes";//打印次数
    public static final String SHOP_PRINT="shopPrint";//主店接单打印机
    public static final String CPSUBORDER="cpSubOrder";//CP子订单
    public static final String WORKERNAME="workerName";//员工姓名
    public static final String WORKERMOBILE="workerMobile";//员工手机
    public static final String CONSUMERDESC = "consumerDesc";//消费者留言
    public static final String MANAGERDESC = "managerDesc";//管理员操作记录

    public static final String OPTIONLOG = "optionlog";//操作日志
    public static final String SHOPDESC = "shopdesc";//店家备注

    public static final String LONGITUDE = "longitude";//经度
    public static final String LATITUDE = "latitude";//纬度
    public static final String ZIPCODE = "zipcode";//邮编
    public static final String CITYCODE = "citycode";//城市码
    public static final String DELIVERYINFO = "deliveryInfo"; //物流订单信息
    public static final String DELIVERYTYPE = "deliveryType"; //物流类型
    public static final String GOODSPICURL = "goodsPicUrl"; //商品图片url
    public static final String USERADDRESSINFO = "userAddressInfo"; //用户地址信息
    public static final String DEDUCFEE = "deductFee";//违约金
    public static final String MQTAG = "mqTag"; //设置mqtag
    public static final String MQBODY = "mqBody"; //转给mq系统的主数据内容
    public static final String PAYTYPE = "payType";//支付类型
    public static final String ISPAY = "isPay";//是否支付
    public static final String PREPAYID = "prepayId";//预支付标识
    public static final String CODEURL = "codeUrl";//可将该参数值生成二维码展示出来进行扫码支付
    public static final String QRCodeUrl = "qrCodeUrl";//小程序码图片地址key
    public static final String POINTTOTAL = "pointTotal";
    public static final String POINT = "point";
    public static final String MONEY = "money";
    public static final String ISSLEEP = "isSleep";//是否已经休息
    public static final String PayMode = "payMode";//先付款还是后付款模式
    public static final String PRICE = "price";//打折价
    public static final String DISPRICE = "disPrice";//打折价
    public static final String INVENTORY = "inventory";//库存

    public static final String RESULTPAYINFO = "resultPayInfo";//支付结果
    public static final String BIZPAYWEIXINURL = "bizPayWeixinUrl";//微信扫码支付url
    public static final String BIZPAYALIURL = "bizPayAliUrl";//支付宝扫码支付url

    public static final String BackColor = "backColor";//背景色
    public static final String YEAR = "year";//年
    public static final String MONTH = "month";//月
    public static final String DAY = "day";//日

    public static final String BILLID = "billid";//帐单id
    public static final String BILLTYPE = "billtype";//帐单类型





}
