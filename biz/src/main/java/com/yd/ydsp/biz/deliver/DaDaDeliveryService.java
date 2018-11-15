package com.yd.ydsp.biz.deliver;

import com.yd.ydsp.biz.deliver.model.DADACallbackData;
import com.yd.ydsp.client.domian.UserOrderDeliveryInfoVO;
import com.yd.ydsp.client.domian.openshop.DeliveryOrderPriceVO;

import java.math.BigDecimal;
import java.util.Map;

public interface DaDaDeliveryService {


    /**
     * 查询此渠道地址信息是否已经同步到了达达
     * @param channelId
     * @return
     */
    boolean queryStationIsExist(String channelId) throws Exception;

    boolean createStation(String channelId,String stationName,Integer business,String cityName,String areaName, String stationAddress,Double lng,Double lat, String contactName,String phone) throws Exception;

    boolean updateStation(String channelId,String cityName,String areaName, String stationAddress,Double lng,Double lat, String contactName,String phone) throws Exception;

    /**
     * 查询订单运费接口
     传入订单相关参数可以查询到该时刻订单所需的运费，同时返回一个唯一的平台订单编号，注意：该平台订单编号有效期为3分钟。
     shop_no	String	是	门店编号，门店创建后可在门店列表和单页查看
     origin_id	String	是	第三方订单ID
     city_code	String	是	订单所在城市的code（查看各城市对应的code值）
     cargo_price	Double	是	订单金额
     is_prepay	Integer	是	是否需要垫付 1:是 0:否 (垫付订单金额，非运费)
     receiver_name	String	是	收货人姓名
     receiver_address	String	是	收货人地址
     callback	String	是	回调URL（查看回调说明）
     receiver_lat	Double	否	收货人地址维度（高德坐标系）
     receiver_lng	Double	否	收货人地址经度（高德坐标系）
     receiver_phone	String	否	收货人手机号（手机号和座机号必填一项）
     receiver_tel	String	否	收货人座机号（手机号和座机号必填一项）
     tips	Double	否	小费（单位：元，精确小数点后一位）
     info	String	否	订单备注
     cargo_type	Integer	否	订单商品类型：食品小吃-1,饮料-2,鲜花-3,文印票务-8,便利店-9,水果生鲜-13,同城电商-19, 医药-20,蛋糕-21,酒品-24,小商品市场-25,服装-26,汽修零配-27,数码-28,小龙虾-29, 其他-5
     cargo_weight	Double	否	订单重量（单位：Kg）
     cargo_num	Integer	否	订单商品数量
     invoice_title	String	否	发票抬头
     origin_mark	String	否	订单来源标示（该字段可以显示在达达app订单详情页面，只支持字母，最大长度为10）
     origin_mark_no	String	否	订单来源编号（该字段可以显示在达达app订单详情页面，支持字母和数字，最大长度为30）
     is_use_insurance	Integer	否
     是否使用保价费（0：不使用保价，1：使用保价； 同时，请确保填写了订单金额（cargo_price））
     商品保价费(当商品出现损坏，可获取一定金额的赔付)
     保费=配送物品实际价值*费率（5‰），配送物品价值及最高赔付不超过10000元， 最高保费为50元（物品价格最小单位为100元，不足100元部分按100元认定，保价费向上取整数， 如：物品声明价值为201元，保价费为300元*5‰=1.5元，取整数为2元。）
     若您选择不保价，若物品出现丢失或损毁，最高可获得平台30元优惠券。 （优惠券直接存入用户账户中）。
     is_finish_code_needed	Integer	否	收货码（0：不需要；1：需要。收货码的作用是：骑手必须输入收货码才能完成订单妥投）
     delay_publish_time	Integer	否	预约发单时间（预约时间unix时间戳(10位),精确到分;整10分钟为间隔，并且需要至少提前20分钟预约。）
     is_direct_delivery	Integer	否	是否选择直拿直送（0：不需要；1：需要。选择直拿只送后，同一时间骑士只能配送此订单至完成，同时，也会相应的增加配送费用）
     * @param orderid
     * @return
     *  {
    "distance": 53459.98,
    "fee": 51.0
    "deliverFee": 51.0
    "deliveryNo":"Ddada27000000001654"
    }
     */
    DeliveryOrderPriceVO queryDeliverFee(String channelId, String orderid, String cityName, BigDecimal price, String receiverName, String receiverAddress,
                                         Double lng, Double lat, String receiverPhone, String info, Integer cargoType, Double cargoWeight) throws Exception;

    /**
     * 根据【查询订单运费接口】返回的平台订单编号进行发单。只有新订单或者状态为已取消、已过期以及投递异常的情况下可以进行订单预发布
     * @param deliveryNo
     * @return
     */
    boolean addAfterQuery(String deliveryNo) throws Exception;


    /**
     * 查询订单的相关信息以及骑手的相关信息
     * @param orderId
     * @return
     * orderId	String	第三方订单编号
    statusCode	Integer	订单状态(待接单＝1 待取货＝2 配送中＝3 已完成＝4 已取消＝5 已过期＝7 指派单=8 妥投异常之物品返回中=9 妥投异常之物品返回完成=10 系统故障订单发布失败=1000 可参考文末的状态说明）
    statusMsg	String	订单状态
    transporterName	String	骑手姓名
    transporterPhone	String	骑手电话
    transporterLng	String	骑手经度
    transporterLat	String	骑手纬度
    deliveryFee	Double	配送费,单位为元
    tips	Double	小费,单位为元
    couponFee	Double	优惠券费用,单位为元
    insuranceFee	Double	保价费,单位为元
    actualFee	Double	实际支付费用,单位为元
    distance	Double	配送距离,单位为米
    createTime	String	发单时间
    acceptTime	String	接单时间,若未接单,则为空
    fetchTime	String	取货时间,若未取货,则为空
    finishTime	String	送达时间,若未送达,则为空
    cancelTime	String	取消时间,若未取消,则为空
    orderFinishCode	String	收货码
     */
    UserOrderDeliveryInfoVO queryOrderInfo(String orderId) throws Exception;

    /**
     *在订单待接单或待取货情况下，调用此接口可取消订单。注意：接单后1－15分钟内取消订单，运费退回。同时扣除2元作为给配送员的违约金
     * order_id	String	是	第三方订单编号
     cancel_reason_id	Integer	是	取消原因ID
     cancel_reason	String	否	取消原因(当取消原因ID为其他时，此字段必填)
     * @param orderId
     * @param reasonId
     * @param reason
     * @return
     * deduct_fee	Double	扣除的违约金(单位：元)
     */
    Map<String,Object> cancelOrder(String orderId, Integer reasonId, String reason) throws Exception;

    /**
     * client_id	String	是	返回达达运单号，默认为空
     order_id	String	是	添加订单接口中的origin_id值
     order_status	int	是	订单状态(待接单＝1 待取货＝2 配送中＝3 已完成＝4 已取消＝5 已过期＝7 指派单=8 妥投异常之物品返回中=9 妥投异常之物品返回完成=10 创建达达运单失败=1000 可参考文末的状态说明）
     cancel_reason	String	是	订单取消原因,其他状态下默认值为空字符串
     cancel_from	Int	是	订单取消原因来源(1:达达配送员取消；2:商家主动取消；3:系统或客服取消；0:默认值)
     update_time	Int	是	更新时间,时间戳
     signature	String	是	对client_id, order_id, update_time的值进行字符串升序排列，再连接字符串，取md5值
     dm_id	Int	否	达达配送员id，接单以后会传
     dm_name	String	否	配送员姓名，接单以后会传
     dm_mobile	String	否	配送员手机号，接单以后会传
     * @param callbackData
     */
    void dadaCalback(DADACallbackData callbackData) throws Exception;

    /**
     * 取城市的code
     * @param cityName
     * @return 返回null表明不支持此城市的物流订单
     */
    String getCityCode(String cityName);


}
