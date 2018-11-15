package com.yd.ydsp.biz.opensearch.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.opensearch.DocumentClient;
import com.aliyun.opensearch.OpenSearchClient;
import com.aliyun.opensearch.SearcherClient;
import com.aliyun.opensearch.sdk.dependencies.com.google.common.collect.Lists;
import com.aliyun.opensearch.sdk.dependencies.com.google.common.collect.Maps;
import com.aliyun.opensearch.sdk.generated.OpenSearch;
import com.aliyun.opensearch.sdk.generated.search.*;
import com.aliyun.opensearch.sdk.generated.search.general.SearchResult;
import com.aliyun.opensearch.sdk.generated.commons.OpenSearchClientException;
import com.aliyun.opensearch.sdk.generated.commons.OpenSearchException;
import com.aliyun.opensearch.sdk.generated.commons.OpenSearchResult;
import com.yd.ydsp.biz.config.DiamondYdWareConfigHolder;
import com.yd.ydsp.biz.customer.model.order.ShopOrder2C;
import com.yd.ydsp.biz.customer.model.order.ShoppingOrderSkuVO;
import com.yd.ydsp.biz.opensearch.OrderSearchService;
import com.yd.ydsp.client.domian.UserAddressInfoVO;
import com.yd.ydsp.client.domian.openshop.SearchOrderDataResultVO;
import com.yd.ydsp.client.domian.openshop.SearchOrderDataVO;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.enums.DeliveryTypeEnum;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.AmountUtils;
import com.yd.ydsp.common.utils.DateUtils;
import com.yd.ydsp.dal.entity.YdShopConsumerOrder;
import netscape.javascript.JSObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;

public class OrderSearchServiceImpl implements OrderSearchService {

    public static final Logger logger = LoggerFactory.getLogger(OrderSearchServiceImpl.class);

    private String appName="ydjsdata";
    private String appNameBackUp="ydjsdata";
    private String orderTableName = "yd_shop_consumer_order";
    private String accesskey;
    private String secret;
    private String host;
    private List resultFields = null;

    private OpenSearch openSearch;
    private OpenSearchClient serviceClient;
    private SearcherClient searcherClient;

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void init(){
        openSearch = new OpenSearch(this.accesskey,this.secret,this.host);
        serviceClient = new OpenSearchClient(openSearch);
        searcherClient = new SearcherClient(serviceClient);
        resultFields = Lists.newArrayList("id", "shopid", "order_name", "channel_id", "orderid",
                "order_type", "pay_mode","is_pay","status","total_amount","user_description",
                "revice_user_moble","is_indoor","order_date","feature");
    }


    @Override
    public boolean commitOrderData(YdShopConsumerOrder consumerOrder) {

        boolean result = false;

        if(consumerOrder==null){
            return true;
        }
        ShopOrder2C shopOrder2C = JSON.parseObject(consumerOrder.getFeature(),ShopOrder2C.class);
        UserAddressInfoVO addressInfoVO = null;
        if(StringUtil.isNotEmpty(consumerOrder.getDelivery())){
            Map<String,Object> deliveryMap = JSON.parseObject(consumerOrder.getDelivery(),Map.class);
            if(deliveryMap.containsKey(Constant.USERADDRESSINFO)) {
                JSONObject adrressInfo = (JSONObject) deliveryMap.get(Constant.USERADDRESSINFO);
                addressInfoVO = adrressInfo.toJavaObject(UserAddressInfoVO.class);
            }
        }

        List<ShoppingOrderSkuVO> shoppingOrderSkuVOList = shopOrder2C.getShoppingOrderSkuVOList();

        TypeEnum enterTypeEnum = TypeEnum.getTypeOfSN(shopOrder2C.getEnterCode());

        SearchOrderDataVO searchOrderDataVO = new SearchOrderDataVO();
        searchOrderDataVO.setId(consumerOrder.getId());
        searchOrderDataVO.setShopid(consumerOrder.getShopid());
        searchOrderDataVO.setChannelId(consumerOrder.getChannelId());
        searchOrderDataVO.setOrderName(consumerOrder.getOrderName());
        searchOrderDataVO.setOpenid(consumerOrder.getOpenid());
        searchOrderDataVO.setWeixinConfigId(consumerOrder.getWeixinConfigId());
        searchOrderDataVO.setUnionid(consumerOrder.getUnionid());
        searchOrderDataVO.setOrderid(consumerOrder.getOrderid());
        searchOrderDataVO.setOrderType(consumerOrder.getOrderType());
        searchOrderDataVO.setIsPay(consumerOrder.getIsPay());
        searchOrderDataVO.setPayMode(shopOrder2C.getPayMode());
        searchOrderDataVO.setTotalAmount(AmountUtils.bigDecimal2Str(consumerOrder.getTotalAmount()));
        searchOrderDataVO.setStatus(consumerOrder.getStatus());
        searchOrderDataVO.setUserDescription(shopOrder2C.getDescription());
        if(addressInfoVO!=null) {
            searchOrderDataVO.setReviceUserMobile(addressInfoVO.getMobile());
        }
        searchOrderDataVO.setOrderDay(DateUtils.date2StrWithYearAndMonthAndDay(consumerOrder.getCreateDate()));
        searchOrderDataVO.setOrderMonth(DateUtils.date2StrWithYearAndMonth(consumerOrder.getCreateDate()));
        searchOrderDataVO.setOrderWeek(DateUtils.date2StrWithYearAndWeek(consumerOrder.getCreateDate()));
        searchOrderDataVO.setOrderDate(DateUtils.date2String(consumerOrder.getCreateDate()));
        searchOrderDataVO.setDeliveryType(DeliveryTypeEnum.nameOf(shopOrder2C.getDeliveryType()).getName());
        if(enterTypeEnum==TypeEnum.DININGTABLE){
            searchOrderDataVO.setIsIndoor(1);
        }else {
            searchOrderDataVO.setIsIndoor(0);
        }
        if(shoppingOrderSkuVOList!=null&&shoppingOrderSkuVOList.size()>0){
            searchOrderDataVO.setGoodsPicUrl(shoppingOrderSkuVOList.get(shoppingOrderSkuVOList.size()-1).getSkuImgUrl());
        }else {
            searchOrderDataVO.setGoodsPicUrl(DiamondYdWareConfigHolder.getInstance().getDefaultGoodsPicUrl());
        }

        Map<String, Object> commitDoc = Maps.newLinkedHashMap();
        commitDoc.put("id", searchOrderDataVO.getId());
        commitDoc.put("shopid", searchOrderDataVO.getShopid());
        commitDoc.put("order_name", searchOrderDataVO.getOrderName());
        commitDoc.put("channel_id", searchOrderDataVO.getChannelId());
        commitDoc.put("openid", searchOrderDataVO.getOpenid());
        commitDoc.put("unionid", searchOrderDataVO.getUnionid());
        commitDoc.put("weixin_config_id", searchOrderDataVO.getWeixinConfigId());
        commitDoc.put("orderid", searchOrderDataVO.getOrderid());
        commitDoc.put("order_type", searchOrderDataVO.getOrderType());
        commitDoc.put("pay_mode", searchOrderDataVO.getPayMode());
        commitDoc.put("is_pay", searchOrderDataVO.getIsPay());
        commitDoc.put("total_amount", searchOrderDataVO.getTotalAmount());
        commitDoc.put("status", searchOrderDataVO.getStatus());
        commitDoc.put("user_description", searchOrderDataVO.getUserDescription());
        commitDoc.put("revice_user_moble", searchOrderDataVO.getReviceUserMobile());
        commitDoc.put("order_day", searchOrderDataVO.getOrderDay());
        commitDoc.put("order_week", searchOrderDataVO.getOrderWeek());
        commitDoc.put("order_month", searchOrderDataVO.getOrderMonth());
        commitDoc.put("order_date", searchOrderDataVO.getOrderDate());
        commitDoc.put("is_indoor",searchOrderDataVO.getIsIndoor());
        Map<String,Object> featureMap = new HashMap<>();
        featureMap.put(Constant.DELIVERYTYPE,searchOrderDataVO.getDeliveryType());
        featureMap.put(Constant.GOODSPICURL,searchOrderDataVO.getGoodsPicUrl());
        commitDoc.put("feature",JSON.toJSONString(featureMap));

        DocumentClient documentClient = new DocumentClient(serviceClient);
        documentClient.add(commitDoc);

        try {

            OpenSearchResult osr = documentClient.commit(this.appName, this.orderTableName);
            if(osr.getResult().equalsIgnoreCase("true")){
                logger.info("用户方推送无报错！\n以下为getTraceInfo推送请求Id:"+osr.getTraceInfo().getRequestId());
                result = true;
            }else{
                logger.error("用户方推送报错！"+osr.getTraceInfo());
                logger.error("searchOrderDoc is : "+commitDoc.toString());

            }

        }catch (OpenSearchClientException oce){
            logger.error("searchOrderDoc is : "+commitDoc.toString());
            logger.error("OpenSearchClientException is :",oce);

        }catch (OpenSearchException ose){
            logger.error("searchOrderDoc is : "+commitDoc.toString());
            logger.error("OpenSearchException is :",ose);
        }

        return result;
    }

    @Override
    public List<SearchOrderDataResultVO> queryAllUserOrder(String unionid, String shopid, Integer pageNum, Integer pageSize) {
        Config config = new Config(Lists.newArrayList(appName));
        if(pageSize>20){
            pageSize = 20;
        }
        config.setStart(pageNum*pageSize-pageSize);
        config.setHits(pageSize);
        config.setSearchFormat(SearchFormat.JSON);
        config.setFetchFields(this.resultFields);
        // 创建参数对象
        SearchParams searchParams = new SearchParams(config);
        searchParams.setQuery("unionid:'"+unionid+"' AND shopid:'"+shopid+"'");
        Sort sorter = new Sort();
        sorter.addToSortFields(new SortField("id", Order.DECREASE)); //设置id字段降序
        //添加Sort对象参数
        searchParams.setSort(sorter);

        List<SearchOrderDataResultVO> result = null;

        try{
            SearchResult searchResult = searcherClient.execute(searchParams);
            String resultStr = searchResult.getResult();
            result = this.doMap(resultStr);

        }catch (OpenSearchClientException oce){
            logger.error("queryAllUserOrder OpenSearchClientException is :",oce);

        }catch (OpenSearchException ose){
            logger.error("queryAllUserOrder OpenSearchException is :",ose);
        }

        return result;
    }

    @Override
    public List<SearchOrderDataResultVO> queryUserOrderByNeedPay(String unionid, String shopid, Integer pageNum, Integer pageSize) {
        Config config = new Config(Lists.newArrayList(appName));
        if(pageSize>20){
            pageSize = 20;
        }
        config.setStart(pageNum*pageSize-pageSize);
        config.setHits(pageSize);
        config.setSearchFormat(SearchFormat.JSON);
        config.setFetchFields(this.resultFields);
        // 创建参数对象
        SearchParams searchParams = new SearchParams(config);
        searchParams.setQuery("unionid:'"+unionid+"' AND shopid:'"+shopid+"'");
        searchParams.setFilter("(status=1 OR status=8) AND is_pay=-1");
        Sort sorter = new Sort();
        sorter.addToSortFields(new SortField("id", Order.DECREASE)); //设置id字段降序
        //添加Sort对象参数
        searchParams.setSort(sorter);

        List<SearchOrderDataResultVO> result = null;

        try{
            SearchResult searchResult = searcherClient.execute(searchParams);
            String resultStr = searchResult.getResult();
            result = this.doMap(resultStr);

        }catch (OpenSearchClientException oce){
            logger.error("queryUserOrderByNeedPay OpenSearchClientException is :",oce);

        }catch (OpenSearchException ose){
            logger.error("queryUserOrderByNeedPay OpenSearchException is :",ose);
        }

        return result;
    }

    @Override
    public List<SearchOrderDataResultVO> queryUserOrderByWaitSendOut(String unionid, String shopid, Integer pageNum, Integer pageSize) {
        Config config = new Config(Lists.newArrayList(appName));
        if(pageSize>20){
            pageSize = 20;
        }
        config.setStart(pageNum*pageSize-pageSize);
        config.setHits(pageSize);
        config.setSearchFormat(SearchFormat.JSON);
        config.setFetchFields(this.resultFields);
        // 创建参数对象
        SearchParams searchParams = new SearchParams(config);
        searchParams.setQuery("unionid:'"+unionid+"' AND shopid:'"+shopid+"'");
        searchParams.setFilter("status=0 OR status=2 OR status=3");
        Sort sorter = new Sort();
        sorter.addToSortFields(new SortField("id", Order.DECREASE)); //设置id字段降序
        //添加Sort对象参数
        searchParams.setSort(sorter);

        List<SearchOrderDataResultVO> result = null;

        try{
            SearchResult searchResult = searcherClient.execute(searchParams);
            String resultStr = searchResult.getResult();
            result = this.doMap(resultStr);

        }catch (OpenSearchClientException oce){
            logger.error("queryUserOrderByWaitSendOut OpenSearchClientException is :",oce);

        }catch (OpenSearchException ose){
            logger.error("queryUserOrderByWaitSendOut OpenSearchException is :",ose);
        }

        return result;
    }

    @Override
    public List<SearchOrderDataResultVO> queryUserOrderBySendOut(String unionid, String shopid, Integer pageNum, Integer pageSize) {
        Config config = new Config(Lists.newArrayList(appName));
        if(pageSize>20){
            pageSize = 20;
        }
        config.setStart(pageNum*pageSize-pageSize);
        config.setHits(pageSize);
        config.setSearchFormat(SearchFormat.JSON);
        config.setFetchFields(this.resultFields);
        // 创建参数对象
        SearchParams searchParams = new SearchParams(config);
        searchParams.setQuery("unionid:'"+unionid+"' AND shopid:'"+shopid+"'");
        searchParams.setFilter("status=5");
        Sort sorter = new Sort();
        sorter.addToSortFields(new SortField("id", Order.DECREASE)); //设置id字段降序
        //添加Sort对象参数
        searchParams.setSort(sorter);

        List<SearchOrderDataResultVO> result = null;

        try{
            SearchResult searchResult = searcherClient.execute(searchParams);
            String resultStr = searchResult.getResult();
            result = this.doMap(resultStr);

        }catch (OpenSearchClientException oce){
            logger.error("queryUserOrderBySendOut OpenSearchClientException is :",oce);

        }catch (OpenSearchException ose){
            logger.error("queryUserOrderBySendOut OpenSearchException is :",ose);
        }

        return result;
    }


    /**
     *-------------------------------------商家进行订单查询---------------------------------
     */

    @Override
    public List<SearchOrderDataResultVO> queryShopAllOrderIndoor(String shopid, Date date, List<Integer> status, Integer pageNum, Integer pageSize) {
        Config config = new Config(Lists.newArrayList(appName));
        if(pageSize>20){
            pageSize = 20;
        }
        config.setStart(pageNum*pageSize-pageSize);
        config.setHits(pageSize);
        config.setSearchFormat(SearchFormat.JSON);
        config.setFetchFields(this.resultFields);
        // 创建参数对象
        SearchParams searchParams = new SearchParams(config);
        String orderDay = DateUtils.date2StrWithYearAndMonthAndDay(date);
        searchParams.setQuery("order_day:'"+orderDay+"' AND shopid:'"+shopid+"'");
        searchParams.setFilter("is_indoor=1"+this.getStatusCondition(status));
        Sort sorter = new Sort();
        sorter.addToSortFields(new SortField("id", Order.DECREASE)); //设置id字段降序
        //添加Sort对象参数
        searchParams.setSort(sorter);

        List<SearchOrderDataResultVO> result = null;

        try{
            SearchResult searchResult = searcherClient.execute(searchParams);
            String resultStr = searchResult.getResult();
            result = this.doMap(resultStr);

        }catch (OpenSearchClientException oce){
            logger.error("queryShopAllOrderIndoor OpenSearchClientException is :",oce);

        }catch (OpenSearchException ose){
            logger.error("queryShopAllOrderIndoor OpenSearchException is :",ose);
        }

        return result;
    }



    @Override
    public List<SearchOrderDataResultVO> queryShopOrderIndoorByTableId(String shopid, String tableId, Date date, List<Integer> status, Integer pageNum, Integer pageSize) {
        Config config = new Config(Lists.newArrayList(appName));
        if(pageSize>20){
            pageSize = 20;
        }
        config.setStart(pageNum*pageSize-pageSize);
        config.setHits(pageSize);
        config.setSearchFormat(SearchFormat.JSON);
        config.setFetchFields(this.resultFields);
        // 创建参数对象
        SearchParams searchParams = new SearchParams(config);
        String orderDay = DateUtils.date2StrWithYearAndMonthAndDay(date);
        searchParams.setQuery("order_day:'"+orderDay+"' AND shopid:'"+shopid+"'");
        searchParams.setFilter("is_indoor=1 AND channel_id="+tableId+this.getStatusCondition(status));
        Sort sorter = new Sort();
        sorter.addToSortFields(new SortField("id", Order.DECREASE)); //设置id字段降序
        //添加Sort对象参数
        searchParams.setSort(sorter);

        List<SearchOrderDataResultVO> result = null;

        try{
            SearchResult searchResult = searcherClient.execute(searchParams);
            String resultStr = searchResult.getResult();
            result = this.doMap(resultStr);

        }catch (OpenSearchClientException oce){
            logger.error("queryShopOrderIndoorByTableId OpenSearchClientException is :",oce);

        }catch (OpenSearchException ose){
            logger.error("queryShopOrderIndoorByTableId OpenSearchException is :",ose);
        }

        return result;
    }


    @Override
    public List<SearchOrderDataResultVO> queryShopOnlineOrder(String shopid, Date date, List<Integer> status, Integer pageNum, Integer pageSize) {
        Config config = new Config(Lists.newArrayList(appName));
        if(pageSize>20){
            pageSize = 20;
        }
        config.setStart(pageNum*pageSize-pageSize);
        config.setHits(pageSize);
        config.setSearchFormat(SearchFormat.JSON);
        config.setFetchFields(this.resultFields);
        // 创建参数对象
        SearchParams searchParams = new SearchParams(config);
        String orderDay = DateUtils.date2StrWithYearAndMonthAndDay(date);
        searchParams.setQuery("order_day:'"+orderDay+"' AND shopid:'"+shopid+"'");
        searchParams.setFilter("is_indoor=0"+this.getStatusCondition(status));
        Sort sorter = new Sort();
        sorter.addToSortFields(new SortField("id", Order.DECREASE)); //设置id字段降序
        //添加Sort对象参数
        searchParams.setSort(sorter);

        List<SearchOrderDataResultVO> result = null;

        try{
            SearchResult searchResult = searcherClient.execute(searchParams);
            String resultStr = searchResult.getResult();
            result = this.doMap(resultStr);

        }catch (OpenSearchClientException oce){
            logger.error("queryShopAllOrder OpenSearchClientException is :",oce);

        }catch (OpenSearchException ose){
            logger.error("queryShopAllOrder OpenSearchException is :",ose);
        }

        return result;
    }


    @Override
    public List<SearchOrderDataResultVO> queryShopOnlineOrderByChannelId(String shopid, String channelId, Date date, List<Integer> status, Integer pageNum, Integer pageSize) {
        Config config = new Config(Lists.newArrayList(appName));
        if(pageSize>20){
            pageSize = 20;
        }
        config.setStart(pageNum*pageSize-pageSize);
        config.setHits(pageSize);
        config.setSearchFormat(SearchFormat.JSON);
        config.setFetchFields(this.resultFields);
        // 创建参数对象
        SearchParams searchParams = new SearchParams(config);
        String orderDay = DateUtils.date2StrWithYearAndMonthAndDay(date);
        searchParams.setQuery("order_day:'"+orderDay+"' AND shopid:'"+shopid+"'");
        searchParams.setFilter("is_indoor=0 AND channel_id='"+channelId+this.getStatusCondition(status));
        Sort sorter = new Sort();
        sorter.addToSortFields(new SortField("id", Order.DECREASE)); //设置id字段降序
        //添加Sort对象参数
        searchParams.setSort(sorter);

        List<SearchOrderDataResultVO> result = null;

        try{
            SearchResult searchResult = searcherClient.execute(searchParams);
            String resultStr = searchResult.getResult();
            result = this.doMap(resultStr);

        }catch (OpenSearchClientException oce){
            logger.error("queryShopAllOrderByChannelId OpenSearchClientException is :",oce);

        }catch (OpenSearchException ose){
            logger.error("queryShopAllOrderByChannelId OpenSearchException is :",ose);
        }

        return result;
    }

    @Override
    public List<SearchOrderDataResultVO> queryShopAllOrder(String shopid, String enterCode, Date date, List<Integer> status, Integer pageNum, Integer pageSize) {
        Config config = new Config(Lists.newArrayList(appName));
        if(pageSize>20){
            pageSize = 20;
        }
        config.setStart(pageNum*pageSize-pageSize);
        config.setHits(pageSize);
        config.setSearchFormat(SearchFormat.JSON);
        config.setFetchFields(this.resultFields);
        // 创建参数对象
        SearchParams searchParams = new SearchParams(config);
        String orderDay = DateUtils.date2StrWithYearAndMonthAndDay(date);
        searchParams.setQuery("order_day:'"+orderDay+"' AND shopid:'"+shopid+"'");
        String filter = "";
        if(StringUtil.isNotEmpty(enterCode)){
            filter = "channel_id='"+enterCode;
        }
        filter = filter + this.getStatusCondition(status);
        if(StringUtil.isNotEmpty(filter)) {
            searchParams.setFilter(filter);
        }
        Sort sorter = new Sort();
        sorter.addToSortFields(new SortField("id", Order.DECREASE)); //设置id字段降序
        //添加Sort对象参数
        searchParams.setSort(sorter);

        List<SearchOrderDataResultVO> result = null;

        try{
            SearchResult searchResult = searcherClient.execute(searchParams);
            String resultStr = searchResult.getResult();
            result = this.doMap(resultStr);

        }catch (OpenSearchClientException oce){
            logger.error("queryShopAllOrder OpenSearchClientException is :",oce);

        }catch (OpenSearchException ose){
            logger.error("queryShopAllOrder OpenSearchException is :",ose);
        }

        return result;
    }

    @Override
    public List<SearchOrderDataResultVO> queryShopAllMoneyOrder(String shopid, Date date, List<Integer> status, Integer pageNum, Integer pageSize) {
        Config config = new Config(Lists.newArrayList(appName));
        if(pageSize>20){
            pageSize = 20;
        }
        config.setStart(pageNum*pageSize-pageSize);
        config.setHits(pageSize);
        config.setSearchFormat(SearchFormat.JSON);
        config.setFetchFields(this.resultFields);
        // 创建参数对象
        SearchParams searchParams = new SearchParams(config);
        String orderDay = DateUtils.date2StrWithYearAndMonthAndDay(date);
        searchParams.setQuery("order_day:'"+orderDay+"' AND shopid:'"+shopid+"'");
        searchParams.setFilter("is_indoor=2"+this.getStatusCondition(status));
        Sort sorter = new Sort();
        sorter.addToSortFields(new SortField("id", Order.DECREASE)); //设置id字段降序
        //添加Sort对象参数
        searchParams.setSort(sorter);

        List<SearchOrderDataResultVO> result = null;

        try{
            SearchResult searchResult = searcherClient.execute(searchParams);
            String resultStr = searchResult.getResult();
            result = this.doMap(resultStr);

        }catch (OpenSearchClientException oce){
            logger.error("queryShopAllMoneyOrder OpenSearchClientException is :",oce);

        }catch (OpenSearchException ose){
            logger.error("queryShopAllMoneyOrder OpenSearchException is :",ose);
        }

        return result;
    }

    protected List<SearchOrderDataResultVO> doMap(String resultStr){

        Map<String,Object> resultMap = JSON.parseObject(resultStr,Map.class);
//        logger.info("resultStr is: "+resultStr);
        List<Map> searchResultList = null;
        if(StringUtil.equals(((String)resultMap.get("status")).toUpperCase(),"OK")){
            searchResultList = new ArrayList<>();
            if(resultMap.containsKey("result")) {
                JSONObject jsonObject = ((JSONObject) resultMap.get("result"));
                if(jsonObject.containsKey("items")){
                    JSONArray items = (JSONArray)jsonObject.get("items");
//                    logger.info("items:"+JSON.toJSONString(items));
                    Iterator iterator = items.iterator();
                    while (iterator.hasNext()){
                        JSONObject objectData = (JSONObject)iterator.next();
                        Map<String,Object> objectMap = objectData.toJavaObject(Map.class);
                        searchResultList.add(objectMap);
                    }
                }
            }
        }
        return this.doMap(searchResultList);
    }

    protected List<SearchOrderDataResultVO> doMap(List<Map> resultJson){
        List<SearchOrderDataResultVO> result = null;
        if(resultJson!=null) {
            result = new ArrayList<>();
            for (Map<String, Object> objectMap : resultJson) {
                SearchOrderDataResultVO sod = new SearchOrderDataResultVO();
                sod.setId(Long.parseLong((String) objectMap.get("id")));
                sod.setShopid((String) objectMap.get("shopid"));
                sod.setOrderName((String) objectMap.get("order_name"));
                sod.setChannelId((String) objectMap.get("channel_id"));
                sod.setOrderid((String) objectMap.get("orderid"));
                sod.setOrderType(new Integer((String) objectMap.get("order_type")));
                sod.setPayMode(new Integer((String) objectMap.get("pay_mode")));
                sod.setIsPay(new Integer((String) objectMap.get("is_pay")));
                sod.setTotalAmount((String) objectMap.get("total_amount"));
                sod.setStatus(new Integer((String) objectMap.get("status")));
                sod.setUserDescription((String) objectMap.get("user_description"));
                sod.setReviceUserMobile((String) objectMap.get("revice_user_moble"));
                sod.setIsIndoor(new Integer((String) objectMap.get("is_indoor")));
                sod.setOrderDate((String) objectMap.get("order_date"));
                String feature = ((String)objectMap.get("feature"));
                Map<String,Object> featureMap = JSON.parseObject(feature,Map.class);
                sod.setDeliveryType((String) featureMap.get(Constant.DELIVERYTYPE));
                sod.setGoodsPicUrl((String)featureMap.get(Constant.GOODSPICURL));
                result.add(sod);
            }
        }

        return result;

    }


    protected String getStatusCondition(List<Integer> status){
        if(status==null||status.size()<=0){
            return "";
        }

        if(status.size()==1){
            return " AND status="+status.get(0);
        }

        String statusStr = " AND (";
        for(int i = 0;i<status.size();i++){

            if(i<status.size()-1){
                statusStr = statusStr + "status="+status.get(i)+" OR ";
            }else {
                statusStr = statusStr + "status="+status.get(i)+")";
            }

        }
        return statusStr;
    }


}
