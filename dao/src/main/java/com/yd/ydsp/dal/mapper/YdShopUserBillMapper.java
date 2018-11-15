package com.yd.ydsp.dal.mapper;

import com.yd.ydsp.dal.entity.YdShopUserBill;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YdShopUserBillMapper {
    int deleteByPrimaryKey(Long id);

    int insert(YdShopUserBill record);

    int insertSelective(YdShopUserBill record);

    YdShopUserBill selectByPrimaryKey(Long id);
    YdShopUserBill selectByLastBill(@Param(value = "userid")Long userid,@Param(value = "shopid") String shopid);
    YdShopUserBill selectByLastCashRechargeBill(@Param(value = "userid")Long userid,@Param(value = "shopid") String shopid);

    List<YdShopUserBill> selectByBillIdAll(String billid);
    YdShopUserBill selectByBillId(@Param(value = "billid") String billid, @Param(value = "billtype") Integer billtype);

    YdShopUserBill selectByBillIdRowLock(@Param(value = "billid") String billid, @Param(value = "billtype") Integer billtype);

    List<YdShopUserBill> selectByUserId(@Param(value = "userid")Long userid, @Param(value = "shopid") String shopid,
                                        @Param(value = "inYear") String inYear,
                                        @Param(value = "inMonth") String inMonth,
                                        @Param(value = "indexPoint")Integer indexPoint,
                                        @Param(value = "count")Integer count);

    List<YdShopUserBill> selectByShopIdYear(@Param(value = "shopid") String shopid,@Param(value = "inYear") String inYear,
                                            @Param(value = "indexPoint")Integer indexPoint,
                                            @Param(value = "count")Integer count);

    List<YdShopUserBill> selectByShopIdMonth(@Param(value = "shopid") String shopid,@Param(value = "inYear") String inYear,
                                             @Param(value = "inMonth") String inMonth,
                                             @Param(value = "indexPoint")Integer indexPoint,
                                             @Param(value = "count")Integer count);

    List<YdShopUserBill> selectByShopIdDay(@Param(value = "shopid") String shopid,@Param(value = "inYear") String inYear,
                                             @Param(value = "inMonth") String inMonth,@Param(value = "inDay") String inDay,
                                           @Param(value = "indexPoint")Integer indexPoint,
                                           @Param(value = "count")Integer count);

    int updateByPrimaryKeySelective(YdShopUserBill record);

    int updateByPrimaryKey(YdShopUserBill record);
    int updateStatusByBillId(YdShopUserBill record);
}