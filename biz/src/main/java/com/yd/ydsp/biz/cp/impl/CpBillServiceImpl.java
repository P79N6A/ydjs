package com.yd.ydsp.biz.cp.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.cp.CpBillService;
import com.yd.ydsp.biz.customer.model.Order2CVO;
import com.yd.ydsp.biz.customer.model.ShoppingCartVO;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.client.domian.paypoint.BillReportListVO;
import com.yd.ydsp.client.domian.paypoint.BillReportVO;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.utils.DateUtils;
import com.yd.ydsp.dal.entity.YdConsumerOrder;
import com.yd.ydsp.dal.entity.YdShoporderStatistics;
import com.yd.ydsp.dal.mapper.YdConsumerOrderMapper;
import com.yd.ydsp.dal.mapper.YdPaypointShopInfoMapper;
import com.yd.ydsp.dal.mapper.YdShoporderStatisticsMapper;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CpBillServiceImpl implements CpBillService {

    public static final Logger logger = LoggerFactory.getLogger(CpBillServiceImpl.class);

    @Resource
    private DozerBeanMapper doMapper;
    @Resource
    private YdPaypointShopInfoMapper ydPaypointShopInfoMapper;
    @Resource
    private YdConsumerOrderMapper ydConsumerOrderMapper;
    @Resource
    private YdShoporderStatisticsMapper ydShoporderStatisticsMapper;
    @Resource
    private UserinfoService userinfoService;


    @Override
    public BillReportVO getReportDataByDate(String openid, String shopid, Date date) {
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        return this.getReportData(shopid,date,date);
    }

    @Override
    public BillReportVO getReportDataByWeek(String openid, String shopid, Integer year, Integer week) {
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        Date dateBegin = DateUtils.getDateByWeek(year,week);
        Date dateEnd = DateUtils.plusDay(dateBegin,6);

        return this.getReportData(shopid,dateBegin,dateEnd);
    }

    @Override
    public BillReportVO getReportDataByMonth(String openid, String shopid, Integer year, Integer month) {

        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        Date dateBegin = DateUtils.getFirstDayByMonth(year,month);
        Date dateEnd = DateUtils.getLastDayByMonth(year,month);
        return this.getReportData(shopid,dateBegin,dateEnd);
    }

    @Override
    public List<Order2CVO> getOrder2CByDate(String openid, String shopid, Date queryDate, Integer pageIndex, Integer count) {
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        return this.getOrder2C(shopid,queryDate,queryDate,pageIndex,count);
    }

    @Override
    public List<BillReportListVO> getOrder2CByWeek(String openid, String shopid, Integer year, Integer week, Integer pageIndex, Integer count) {
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        Date dateBegin = DateUtils.getDateByWeek(year,week);
        Date dateEnd = DateUtils.plusDay(dateBegin,6);
        return this.getBillReportList2C(shopid,dateBegin,dateEnd,pageIndex,count);
    }

    @Override
    public List<BillReportListVO> getOrder2CByMonth(String openid, String shopid, Integer year, Integer month, Integer pageIndex, Integer count) {
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        Date dateBegin = DateUtils.getFirstDayByMonth(year,month);
        Date dateEnd = DateUtils.getLastDayByMonth(year,month);
        return this.getBillReportList2C(shopid,dateBegin,dateEnd,pageIndex,count);
    }

    protected BillReportVO getReportData(String shopid, Date beginDate, Date endDate) {

        BillReportVO billReportVO = new BillReportVO();
        billReportVO.setOrderCount(0);
        billReportVO.setReceiveAmount(new BigDecimal("0.00"));
        billReportVO.setReceivecashAmount(new BigDecimal("0.00"));

        List<YdShoporderStatistics> shoporderStatisticsList = ydShoporderStatisticsMapper.selectByOrderDate(shopid,beginDate,endDate,0,100);
        if(shoporderStatisticsList==null||shoporderStatisticsList.size()<=0){
            return billReportVO;
        }

        for(YdShoporderStatistics shoporderStatistics : shoporderStatisticsList){
            billReportVO.setOrderCount(billReportVO.getOrderCount()+shoporderStatistics.getOrderCount());
            billReportVO.setReceiveAmount(billReportVO.getReceiveAmount().add(shoporderStatistics.getReceiveAmount()));
            billReportVO.setReceivecashAmount(billReportVO.getReceivecashAmount().add(shoporderStatistics.getReceivecashAmount()));
        }

        return billReportVO;
    }

    protected List<Order2CVO> getOrder2C(String shopid, Date dateBegin,Date dateEnd, Integer pageIndex, Integer count) {
        if(dateBegin==null||dateEnd==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),ErrorCodeConstants.PARAM_ERROR.getErrorMessage());
        }
        if(pageIndex==null||pageIndex<=0){
            pageIndex = 1;
        }
        if(count==null||count<=0){
            count = 10;
        }
        Integer indexPoint = (pageIndex-1)*count;
        if(indexPoint<=0){
            indexPoint = 0;
        }
        List<Order2CVO> order2CVOList = new ArrayList<>();

        /**
         * 先查统计表
         */
        YdShoporderStatistics shoporderStatistics = ydShoporderStatisticsMapper.selectByShopid(shopid,dateBegin);

        if(shoporderStatistics==null){
            return order2CVOList;
        }

        Long firstId = shoporderStatistics.getFirstid();
        Long lastId = shoporderStatistics.getLastid();
        if(!DateUtils.isSameDate(dateBegin,dateEnd)){
            YdShoporderStatistics shoporderStatisticsEnd = ydShoporderStatisticsMapper.selectByShopid(shopid,dateEnd);
            if(shoporderStatisticsEnd!=null){
                if(shoporderStatisticsEnd.getLastid()>lastId){
                    lastId = shoporderStatisticsEnd.getLastid();
                }
            }
        }

        List<YdConsumerOrder> consumerOrderList = null;
        consumerOrderList = ydConsumerOrderMapper.selectByOrderManager(firstId,
                lastId,shopid,indexPoint,count);


        if(consumerOrderList==null||consumerOrderList.size()<=0){
            return order2CVOList;
        }

        for(YdConsumerOrder consumerOrder: consumerOrderList){
            Order2CVO order2CVO = this.doMapOrder(consumerOrder);
            if(order2CVO!=null){
                order2CVOList.add(order2CVO);
            }

        }

        return order2CVOList;
    }


    protected List<BillReportListVO> getBillReportList2C(String shopid, Date dateBegin,Date dateEnd, Integer pageIndex, Integer count) {
        if(dateBegin==null||dateEnd==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),ErrorCodeConstants.PARAM_ERROR.getErrorMessage());
        }
        if(pageIndex==null||pageIndex<=0){
            pageIndex = 1;
        }
        if(count==null||count<=0){
            count = 10;
        }
        Integer indexPoint = (pageIndex-1)*count;
        if(indexPoint<=0){
            indexPoint = 0;
        }
        List<BillReportListVO> billReportListVOList = new ArrayList<>();

        /**
         * 先查统计表
         */
        List<YdShoporderStatistics> shoporderStatisticsList = ydShoporderStatisticsMapper.selectByOrderDate(shopid,dateBegin,dateEnd,indexPoint,count);

        if(shoporderStatisticsList==null||shoporderStatisticsList.size()<=0){
            return billReportListVOList;
        }

        for(YdShoporderStatistics shoporderStatistics:shoporderStatisticsList){
            billReportListVOList.add(doMapper.map(shoporderStatistics,BillReportListVO.class));
        }

        return billReportListVOList;
    }

    private Order2CVO doMapOrder(YdConsumerOrder consumerOrder){
        if(consumerOrder==null){
            return null;
        }
        ShoppingCartVO cartVO = JSON.parseObject(consumerOrder.getFeature(),ShoppingCartVO.class);
        Order2CVO order2CVO = doMapper.map(cartVO,Order2CVO.class);
        order2CVO.setOrderid(consumerOrder.getOrderid());
        order2CVO.setOrderName(consumerOrder.getOrderName());
        order2CVO.setStatus(consumerOrder.getStatus());
        order2CVO.setOrderType(consumerOrder.getOrderType());
        order2CVO.setPrintCount(consumerOrder.getPrintCount());
        order2CVO.setUseCash(consumerOrder.getUseCash());
        String consumerInfo = consumerOrder.getConsumerDesc();
        if(StringUtil.isNotBlank(consumerInfo)){
            consumerInfo = consumerInfo + "\\r\\n";
        }else {
            consumerInfo = "";
        }
        String managerOptionHistoryInfo = consumerOrder.getManagerOptionHistory();
        if(managerOptionHistoryInfo==null){
            managerOptionHistoryInfo = "";
        }
        managerOptionHistoryInfo = consumerInfo + managerOptionHistoryInfo;

        order2CVO.setDescription(managerOptionHistoryInfo);
        if(order2CVO.getPayMode()==1) {
            order2CVO.setOrderDate(consumerOrder.getModifyDate());
        }else{
            order2CVO.setOrderDate(consumerOrder.getCreateDate());
        }
        return order2CVO;
    }
}
