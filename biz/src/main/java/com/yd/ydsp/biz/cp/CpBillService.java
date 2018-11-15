package com.yd.ydsp.biz.cp;

import com.yd.ydsp.biz.customer.model.Order2CVO;
import com.yd.ydsp.client.domian.paypoint.BillReportListVO;
import com.yd.ydsp.client.domian.paypoint.BillReportVO;

import java.util.Date;
import java.util.List;

public interface CpBillService {

    /**
     * 取指定店的指定日期的报告汇总数据
     * @param openid
     * @param shopid
     * @param date
     * @return
     */
    BillReportVO getReportDataByDate(String openid,String shopid,Date date);

    /**
     * 取指定店指定年周的报告汇总数据
     * @param openid
     * @param shopid
     * @param year
     * @param week
     * @return
     */
    BillReportVO getReportDataByWeek(String openid,String shopid,Integer year, Integer week);

    /**
     * 取指定店的指定年月的报告汇总数据
     * @param openid
     * @param shopid
     * @param year
     * @param month
     * @return
     */
    BillReportVO getReportDataByMonth(String openid,String shopid,Integer year,Integer month);

    /**
     * 报表页中的按天订单查询接口
     * @param openid
     * @param shopid
     * @param queryDate
     * @param pageIndex 从1开始
     * @param count 每页数量
     * @return
     */
    List<Order2CVO> getOrder2CByDate(String openid, String shopid, Date queryDate, Integer pageIndex, Integer count);


    /**
     * 报表页中查一周的每天列表数据
     * @param openid
     * @param shopid
     * @param year
     * @param week
     * @param pageIndex 从1开始
     * @param count 每页数量
     * @return
     */
    List<BillReportListVO> getOrder2CByWeek(String openid, String shopid, Integer year, Integer week, Integer pageIndex, Integer count);

    /**
     * 报表页中查一月的每天列表数据
     * @param openid
     * @param shopid
     * @param year
     * @param month
     * @param pageIndex 从1开始
     * @param count 每页数量
     * @return
     */
    List<BillReportListVO> getOrder2CByMonth(String openid, String shopid, Integer year, Integer month, Integer pageIndex, Integer count);



}
