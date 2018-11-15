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
import com.aliyun.opensearch.sdk.generated.commons.OpenSearchClientException;
import com.aliyun.opensearch.sdk.generated.commons.OpenSearchException;
import com.aliyun.opensearch.sdk.generated.commons.OpenSearchResult;
import com.aliyun.opensearch.sdk.generated.search.*;
import com.aliyun.opensearch.sdk.generated.search.general.SearchResult;
import com.yd.ydsp.biz.opensearch.GoodsSearchService;
import com.yd.ydsp.client.domian.paypoint.WareSkuSearcheVO;
import com.yd.ydsp.client.domian.paypoint.YdCpSpecConfigInfoVO;
import com.yd.ydsp.common.lang.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

public class GoodsSearchServiceImpl implements GoodsSearchService {
    public static final Logger logger = LoggerFactory.getLogger(GoodsSearchServiceImpl.class);

    private String appName="ydgoods";
    private String appNameBackUp="ydgoods";
    private String tableName = "yd_goods_info";
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
        resultFields = Lists.newArrayList("id", "shopid", "name", "wareimg", "wareseq",
                "inventory","status","ismust","price","weight", "wareitem_status","wareitem_name",
                "disprice","unit_name","discount","feature","iscommend","sell_count_month","skuid");
    }

    @Override
    public boolean commitOrderData(String wareItemName, WareSkuSearcheVO wareSkuVO) {
        boolean result = false;

        if(wareSkuVO==null){
            return true;
        }
        Map<String, Object> commitDoc = Maps.newLinkedHashMap();
        commitDoc.put("id",wareSkuVO.getId());
        commitDoc.put("shopid",wareSkuVO.getShopid());
        commitDoc.put("wareitemid",wareSkuVO.getWareitemid());
        commitDoc.put("name",wareSkuVO.getName());
        commitDoc.put("wareimg",wareSkuVO.getWareimg());
        commitDoc.put("description",wareSkuVO.getDescription());
        commitDoc.put("wareseq",wareSkuVO.getWareseq());
        commitDoc.put("inventory",wareSkuVO.getInventory());
        commitDoc.put("status",wareSkuVO.getStatus());
        commitDoc.put("ismust",wareSkuVO.getIsMustWare());
        commitDoc.put("price",wareSkuVO.getPrice());
        commitDoc.put("disprice",wareSkuVO.getDisprice());
        commitDoc.put("unit_name",wareSkuVO.getUnitName());
        commitDoc.put("discount",wareSkuVO.getDiscount());
        commitDoc.put("weight",wareSkuVO.getWeight());
        if(wareSkuVO.getVolume()!=null) {
            commitDoc.put("volume", JSON.toJSONString(wareSkuVO.getVolume()));
        }
        commitDoc.put("create_date", wareSkuVO.getCreateDateInt());
        commitDoc.put("modify_date",wareSkuVO.getModifyDateInt());
        if(wareSkuVO.getSpecConfigInfoVOList()!=null) {
            commitDoc.put("spec_config_info", JSON.toJSONString(wareSkuVO.getSpecConfigInfoVOList()));
        }
        commitDoc.put("iscommend",wareSkuVO.getIsCommend());
        commitDoc.put("sell_count_month",wareSkuVO.getSellCountMonth());
        commitDoc.put("sku_id",wareSkuVO.getSkuid());
        commitDoc.put("tag",wareItemName);
        commitDoc.put("wareitem_name",wareSkuVO.getWareitemName());
        commitDoc.put("wareitem_status",wareSkuVO.getWareitemStatus());

        DocumentClient documentClient = new DocumentClient(serviceClient);
        documentClient.add(commitDoc);

        try {

            OpenSearchResult osr = documentClient.commit(this.appName, this.tableName);
            if(osr.getResult().equalsIgnoreCase("true")){
                logger.info("用户方推送无报错！\n以下为getTraceInfo推送请求Id:"+osr.getTraceInfo().getRequestId());
                result = true;
            }else{
                logger.error("用户方推送报错！"+osr.getTraceInfo());
                logger.error("GoodsSearchService is : "+commitDoc.toString());

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
    public List<WareSkuSearcheVO> queryWareByItemId(String shopid, String wareItemid, List<Integer> status, Integer pageNum, Integer pageSize) {
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
        searchParams.setQuery("shopid:'"+shopid+"' AND wareitemid:'"+wareItemid+"'");
        Sort sorter = new Sort();
        sorter.addToSortFields(new SortField("wareseq", Order.INCREASE)); //设置id字段降序
        /**
         * 过滤
         */
        String filter = "";
        filter = filter + this.getGoodsStatusCondition(status,true)+this.getItemsStatusCondition(status);
        if(StringUtil.isNotEmpty(filter)) {
            searchParams.setFilter(filter);
        }
        //添加Sort对象参数
        searchParams.setSort(sorter);

        List<WareSkuSearcheVO> result = null;

        try{
            SearchResult searchResult = searcherClient.execute(searchParams);
            String resultStr = searchResult.getResult();
            result = this.doMap(resultStr);

        }catch (OpenSearchClientException oce){
            logger.error("queryWareByItemId OpenSearchClientException is :",oce);

        }catch (OpenSearchException ose){
            logger.error("queryWareByItemId OpenSearchException is :",ose);
        }

        return result;
    }

    @Override
    public List<WareSkuSearcheVO> queryWareByItemId(String shopid, String wareItemid, Integer pageNum, Integer pageSize) {
        List<Integer> status = new ArrayList<>();
        status.add(0);
        return this.queryWareByItemId(shopid,wareItemid,status,pageNum,pageSize);
    }

    @Override
    public List<WareSkuSearcheVO> queryWareByShopId(String shopid, String sortName, Integer sortType, List<Integer> status, Integer pageNum, Integer pageSize) {
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
        searchParams.setQuery("shopid:'"+shopid+"'");
        Sort sorter = new Sort();
        sorter.addToSortFields(new SortField(sortName, Order.findByValue(sortType)));
        /**
         * 过滤
         */
        String filter = "";
        filter = filter + this.getGoodsStatusCondition(status,true)+this.getItemsStatusCondition(status);
        if(StringUtil.isNotEmpty(filter)) {
            searchParams.setFilter(filter);
        }
        //添加Sort对象参数
        searchParams.setSort(sorter);

        List<WareSkuSearcheVO> result = null;

        try{
            SearchResult searchResult = searcherClient.execute(searchParams);
            String resultStr = searchResult.getResult();
            result = this.doMap(resultStr);

        }catch (OpenSearchClientException oce){
            logger.error("queryWareByShopId OpenSearchClientException is :",oce);

        }catch (OpenSearchException ose){
            logger.error("queryWareByShopId OpenSearchException is :",ose);
        }

        return result;
    }

    @Override
    public List<WareSkuSearcheVO> queryWareByShopId(String shopid, Integer sortType, List<Integer> status, Integer pageNum, Integer pageSize) {

        return this.queryWareByShopId(shopid,"id",sortType,status,pageNum,pageSize);
    }

    @Override
    public List<WareSkuSearcheVO> queryWareByShopId(String shopid, Integer sortType, Integer pageNum, Integer pageSize) {
        List<Integer> status = new ArrayList<>();
        status.add(0);
        return this.queryWareByShopId(shopid,sortType,status,pageNum,pageSize);
    }

    @Override
    public List<WareSkuSearcheVO> queryWareByNewSku(String shopid, Integer count) {
        List<Integer> status = new ArrayList<>();
        status.add(0);
        return this.queryWareByShopId(shopid,"id",0,status,1,count);
    }

    @Override
    public List<WareSkuSearcheVO> queryWareByHotSku(String shopid, Integer count) {
        List<Integer> status = new ArrayList<>();
        status.add(0);
        return this.queryWareByShopId(shopid,"sell_count_month",0,status,1,count);
    }

    @Override
    public List<WareSkuSearcheVO> queryWareByMustSku(String shopid, List<Integer> status) {
        Config config = new Config(Lists.newArrayList(appName));
        Integer pageSize = 20;
        config.setStart(0);
        config.setHits(pageSize);
        config.setSearchFormat(SearchFormat.JSON);
        config.setFetchFields(this.resultFields);

        // 创建参数对象
        SearchParams searchParams = new SearchParams(config);
        searchParams.setQuery("shopid:'"+shopid+"' AND ismust:'1'");
        Sort sorter = new Sort();
        sorter.addToSortFields(new SortField("wareseq", Order.INCREASE));
        /**
         * 过滤
         */
        String filter = "";
        filter = filter + this.getGoodsStatusCondition(status,true)+this.getItemsStatusCondition(status);
        if(StringUtil.isNotEmpty(filter)) {
            searchParams.setFilter(filter);
        }
        //添加Sort对象参数
        searchParams.setSort(sorter);

        List<WareSkuSearcheVO> result = null;

        try{
            SearchResult searchResult = searcherClient.execute(searchParams);
            String resultStr = searchResult.getResult();
            result = this.doMap(resultStr);

        }catch (OpenSearchClientException oce){
            logger.error("queryWareByMustSku OpenSearchClientException is :",oce);

        }catch (OpenSearchException ose){
            logger.error("queryWareByMustSku OpenSearchException is :",ose);
        }

        return result;
    }

    @Override
    public List<WareSkuSearcheVO> queryWareByMustSku(String shopid) {
        List<Integer> status = new ArrayList<>();
        status.add(0);
        return this.queryWareByMustSku(shopid,status);
    }

    @Override
    public List<WareSkuSearcheVO> queryWareByMoreSku(String shopid,Integer count) {
        List<Integer> status = new ArrayList<>();
        status.add(0);
        return this.queryWareByShopId(shopid,"modify_date",0,status,1,count);
    }

    protected String getGoodsStatusCondition(List<Integer> status,boolean isFirst){
        if(status==null||status.size()<=0){
            return "";
        }

        if(status.size()==1){
            if(isFirst) {
                return "status=" + status.get(0);
            }else {
                return " AND status=" + status.get(0);
            }
        }

        String statusStr = "(";
        if(!isFirst){
            statusStr = " AND (";
        }
        for(int i = 0;i<status.size();i++){

            if(i<status.size()-1){
                statusStr = statusStr + "status="+status.get(i)+" OR ";
            }else {
                statusStr = statusStr + "status="+status.get(i)+")";
            }

        }
        return statusStr;
    }

    protected String getItemsStatusCondition(List<Integer> status){
        if(status==null||status.size()<=0){
            return "";
        }

        if(status.size()==1){
            return " AND wareitem_status="+status.get(0);
        }

        String statusStr = " AND (";
        for(int i = 0;i<status.size();i++){

            if(i<status.size()-1){
                statusStr = statusStr + "wareitem_status="+status.get(i)+" OR ";
            }else {
                statusStr = statusStr + "wareitem_status="+status.get(i)+")";
            }

        }
        return statusStr;
    }


    protected List<WareSkuSearcheVO> doMap(String resultStr){

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

    protected List<WareSkuSearcheVO> doMap(List<Map> resultJson){
        List<WareSkuSearcheVO> result = null;
        if(resultJson!=null) {
            result = new ArrayList<>();
            for (Map<String, String> objectMap : resultJson) {
                WareSkuSearcheVO wareSkuSearcheVO = new WareSkuSearcheVO();
                wareSkuSearcheVO.setId(new Long(objectMap.get("id")));
                wareSkuSearcheVO.setShopid(objectMap.get("shopid"));
                wareSkuSearcheVO.setWareitemid(objectMap.get("wareitemid"));
                wareSkuSearcheVO.setName(objectMap.get("name"));
                wareSkuSearcheVO.setWareimg(objectMap.get("wareimg"));
                wareSkuSearcheVO.setDescription(objectMap.get("description"));
                wareSkuSearcheVO.setWareseq(new Integer(objectMap.get("wareseq")));
                wareSkuSearcheVO.setInventory(new Integer(objectMap.get("inventory")));
                wareSkuSearcheVO.setStatus(new Integer(objectMap.get("status")));
                Integer isMust = new Integer(objectMap.get("ismust"));
                if(isMust.intValue()==0) {
                    wareSkuSearcheVO.setIsMustWare(false);
                }else {
                    wareSkuSearcheVO.setIsMustWare(true);
                }
                wareSkuSearcheVO.setPrice(new BigDecimal(objectMap.get("price")));
                wareSkuSearcheVO.setDisprice(new BigDecimal(objectMap.get("disprice")));
                wareSkuSearcheVO.setUnitName(objectMap.get("unit_name"));
                wareSkuSearcheVO.setDiscount(new BigDecimal(objectMap.get("discount")));
                wareSkuSearcheVO.setWeight(new BigDecimal(objectMap.get("weight")));
                if(StringUtil.isNotEmpty(objectMap.get("volume"))) {
                    wareSkuSearcheVO.setVolume(JSON.parseObject(objectMap.get("volume"), Map.class));
                }
                wareSkuSearcheVO.setCreateDateInt(new Integer(objectMap.get("create_date")));
                wareSkuSearcheVO.setModifyDateInt(new Integer(objectMap.get("modify_date")));
                if(StringUtil.isNotEmpty(objectMap.get("spec_config_info"))) {
                    wareSkuSearcheVO.setSpecConfigInfoVOList(
                            JSON.parseArray(objectMap.get("spec_config_info"), YdCpSpecConfigInfoVO.class));
                }
                Integer isCommd = new Integer(objectMap.get("iscommend"));
                if(isCommd.intValue()==0) {
                    wareSkuSearcheVO.setIsCommend(false);
                }else {
                    wareSkuSearcheVO.setIsCommend(true);
                }
                wareSkuSearcheVO.setSellCountMonth(new Integer(objectMap.get("sell_count_month")));
                wareSkuSearcheVO.setSkuid(objectMap.get("sku_id"));
                wareSkuSearcheVO.setWareitemStatus(new Integer(objectMap.get("wareitem_status")));
                wareSkuSearcheVO.setWareitemName(objectMap.get("wareitem_name"));
            }
        }
        return result;
    }
}
