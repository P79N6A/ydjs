package com.yd.ydsp.biz.cp.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yd.ydsp.biz.config.DiamondWeiXinInfoConfigHolder;
import com.yd.ydsp.biz.config.DiamondYdPayPointConfigHolder;
import com.yd.ydsp.biz.cp.CpService;
import com.yd.ydsp.biz.cp.ShopInfoService;
import com.yd.ydsp.biz.cp.SpecConfigService;
import com.yd.ydsp.biz.cp.model.ShopMainPageVO;
import com.yd.ydsp.biz.customer.model.Order2CVO;
import com.yd.ydsp.biz.customer.model.ShoppingCartSkuVO;
import com.yd.ydsp.biz.customer.model.ShoppingCartVO;
import com.yd.ydsp.biz.customer.model.order.ShopOrder2C;
import com.yd.ydsp.biz.customer.model.order.ShopOrderExt2CP;
import com.yd.ydsp.biz.customer.model.order.ShoppingOrderSkuVO;
import com.yd.ydsp.biz.manage.CpAgentService;
import com.yd.ydsp.biz.message.MqMessageService;
import com.yd.ydsp.biz.opensearch.OrderSearchService;
import com.yd.ydsp.biz.oss.OSSFileService;
import com.yd.ydsp.biz.pay.WeiXinPayService;
import com.yd.ydsp.biz.pay.model.WeiXinPayRequestDO;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.biz.weixin.WeixinSamll2ShopService;
import com.yd.ydsp.biz.yunprinter.FEPrinterService;
import com.yd.ydsp.client.domian.*;
import com.yd.ydsp.client.domian.openshop.SearchOrderDataResultVO;
import com.yd.ydsp.client.domian.paypoint.*;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.constants.paypoint.ShopSupportFlagConstants;
import com.yd.ydsp.common.enums.*;
import com.yd.ydsp.common.enums.paypoint.PrintTypeEnum;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.redis.RedisManager;
import com.yd.ydsp.common.utils.AmountUtils;
import com.yd.ydsp.common.utils.DateUtils;
import com.yd.ydsp.common.utils.RandomUtil;
import com.yd.ydsp.dal.entity.*;
import com.yd.ydsp.dal.mapper.*;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 *
 * @author zengyixun
 * @date 17/9/17
 */
public class CpServiceImpl implements CpService {

    public static final Logger logger = LoggerFactory.getLogger(CpServiceImpl.class);

    @Resource
    private DozerBeanMapper doMapper;
    @Resource
    private YdPaypointShopInfoMapper ydPaypointShopInfoMapper;
    @Resource
    private YdPaypointCpdeviceInfoMapper ydPaypointCpdeviceInfoMapper;
    @Resource
    private YdPaypointDiningtableMapper ydPaypointDiningtableMapper;
    @Resource
    private YdTableQrcodeMapper ydTableQrcodeMapper;
    @Resource
    private YdPaypointContractinfoMapper ydPaypointContractinfoMapper;
    @Resource
    private YdPaypointUserContractMapper ydPaypointUserContractMapper;
    @Resource
    private YdShoporderStatisticsMapper ydShoporderStatisticsMapper;
    @Resource
    private YdPaypointBankInfoMapper ydPaypointBankInfoMapper;
    @Resource
    private YdConsumerOrderMapper ydConsumerOrderMapper;
    @Resource
    private YdPaypointWaresSkuMapper ydPaypointWaresSkuMapper;
    @Resource
    private WeixinSamll2ShopService weixinSamll2ShopService;
    @Resource
    private OSSFileService ossFileService;
    @Resource
    private WeiXinPayService weiXinPayService;
    @Resource
    private YdShopConsumerOrderMapper ydShopConsumerOrderMapper;
    @Resource
    private YdWeixinUserInfoMapper ydWeixinUserInfoMapper;
    @Resource
    private YdWeixinServiceConfigMapper ydWeixinServiceConfigMapper;

    @Resource
    private UserinfoService userinfoService;
    @Resource
    private SpecConfigService specConfigService;
    @Resource
    private FEPrinterService fePrinterService;
    @Resource
    private MqMessageService mqMessageService;
    @Resource
    private OrderSearchService orderSearchService;
    @Resource
    private ShopInfoService shopInfoService;
    @Resource
    private RedisManager redisManager;


    @Resource
    private YdManageUserInfoMapper ydManageUserInfoMapper;
    @Resource
    private YdManageUserWhiteListMapper ydManageUserWhiteListMapper;
    @Resource
    private YdPaypointShopInfoExtMapper ydPaypointShopInfoExtMapper;
    @Resource
    private CpAgentService cpAgentService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String addDevice(String openid, String shopid, String name, String sn, TypeEnum deviceType, String description) {
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdPaypointShopInfo paypointShopInfo = ydPaypointShopInfoMapper.selectByShopIdRowLock(shopid);
        String deviceId = RandomUtil.getSNCode(deviceType);
        YdPaypointCpdeviceInfo cpdeviceInfo = new YdPaypointCpdeviceInfo();
        cpdeviceInfo.setDeviceid(deviceId);
        cpdeviceInfo.setDeviceType(deviceType.getName());
        if(StringUtil.isBlank(sn)){
            sn = deviceId;
        }
        cpdeviceInfo.setSn(sn);
        cpdeviceInfo.setName(name);
        cpdeviceInfo.setShopid(shopid);
        cpdeviceInfo.setDescription(description);
        cpdeviceInfo.setStatus(0);
        if(ydPaypointCpdeviceInfoMapper.selectBySN(sn,deviceType.getName())!=null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"相同序号(SN)设备已经存在！");
        }
        if(ydPaypointCpdeviceInfoMapper.insertSelective(cpdeviceInfo)>0){
            ShopInfoDTO shopInfoDTO = doMapper.map(paypointShopInfo,ShopInfoDTO.class);
            shopInfoDTO.addDeviceValue(deviceType.getName(),deviceId);
            paypointShopInfo.setDeviceInfo(shopInfoDTO.getDeviceInfo());
            if(ydPaypointShopInfoMapper.updateByPrimaryKeySelective(paypointShopInfo)<=0){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"增加设备失败!");
            }
            return deviceId;
        }else {
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"增加设备失败!");
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String addPrinter(String openid, String shopid, String name, String sn,String key, PrintTypeEnum typeEnum, Integer times, String description) {
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        if(StringUtil.isBlank(sn)||StringUtil.isBlank(key)||typeEnum==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"参数不完整，请检查！");
        }
        if(times==null){
            times = 1;
        }
        if(times<=0){
            times = 1;
        }
        TypeEnum deviceType = TypeEnum.PRINTER;
        YdPaypointShopInfo paypointShopInfo = ydPaypointShopInfoMapper.selectByShopIdRowLock(shopid);
        String deviceId = RandomUtil.getSNCode(deviceType);
        YdPaypointCpdeviceInfo cpdeviceInfo = new YdPaypointCpdeviceInfo();
        cpdeviceInfo.setDeviceid(deviceId);
        cpdeviceInfo.setDeviceType(deviceType.getName());
        cpdeviceInfo.setSn(sn+"#"+key);
        cpdeviceInfo.setName(name);
        cpdeviceInfo.setShopid(shopid);
        cpdeviceInfo.setStatus(0);
        cpdeviceInfo.setDescription(description);
        CPDeviceInfoDTO deviceInfoDTO = doMapper.map(cpdeviceInfo,CPDeviceInfoDTO.class);
        deviceInfoDTO.addFeature(Constant.PRINT_TYPE,typeEnum.getName());
        deviceInfoDTO.addFeature(Constant.PRINT_TIMES,times.toString());
        cpdeviceInfo.setFeature(deviceInfoDTO.getFeature());
        if(ydPaypointCpdeviceInfoMapper.selectBySN(cpdeviceInfo.getSn(),deviceType.getName())!=null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"相同序号(SN)打印机已经存在！");
        }
        if(ydPaypointCpdeviceInfoMapper.insert(cpdeviceInfo)>0){
            ShopInfoDTO shopInfoDTO = doMapper.map(paypointShopInfo,ShopInfoDTO.class);
            shopInfoDTO.addDeviceValue(deviceType.getName(),deviceId);
            if(typeEnum==PrintTypeEnum.CASHIER) {
                if (StringUtil.isEmpty(shopInfoDTO.getFeature(Constant.SHOP_PRINT))) {
                    shopInfoDTO.addFeature(Constant.SHOP_PRINT, deviceId);
                }
            }
            paypointShopInfo.setDeviceInfo(shopInfoDTO.getDeviceInfo());
            if(ydPaypointShopInfoMapper.updateByPrimaryKeySelective(paypointShopInfo)<=0){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"增加设备失败!");
            }
            List<String> printerInfo = new ArrayList<>();
            printerInfo.add(cpdeviceInfo.getSn()+"#"+cpdeviceInfo.getName()+"#");
            Map<String,Object> pResult = fePrinterService.printerAddlist(printerInfo);
            logger.info("addPrinter fePrinterService.printerAddlist result: "+JSON.toJSONString(pResult));
            return deviceId;
        }else{
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"增加设备失败!");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean modifyDevice(String openid, String deviceid, String shopid, String name, String sn, TypeEnum deviceType, String description) {
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdPaypointCpdeviceInfo cpdeviceInfo = ydPaypointCpdeviceInfoMapper.selectByDeviceId(deviceid);
        if(cpdeviceInfo==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"设备不存在！");
        }
        if(StringUtil.equals(shopid,cpdeviceInfo.getShopid())&&
                StringUtil.equals(deviceType.getName(),cpdeviceInfo.getDeviceType())){
            YdPaypointCpdeviceInfo paypointCpdeviceInfo = ydPaypointCpdeviceInfoMapper.selectBySN(sn,deviceType.getName());
            if(paypointCpdeviceInfo!=null) {
                if (!StringUtil.equals(paypointCpdeviceInfo.getDeviceid(), cpdeviceInfo.getDeviceid())) {
                    throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "相同序号(SN)设备已经存在，不能修改！");
                }
            }
            if(StringUtil.isBlank(sn)){
                sn = cpdeviceInfo.getSn();
            }
            cpdeviceInfo.setSn(sn);
            cpdeviceInfo.setName(name);
            cpdeviceInfo.setDescription(description);
            if(ydPaypointCpdeviceInfoMapper.updateByPrimaryKeySelective(cpdeviceInfo)<=0){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"修改设备信息失败!");
            }


        }else {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),ErrorCodeConstants.PARAM_ERROR.getErrorMessage());
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean modifyPrinter(String openid, String deviceid, String shopid, String name, String sn,String key, Integer times, PrintTypeEnum typeEnum, String description) {
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        if(StringUtil.isBlank(sn)||StringUtil.isBlank(key)||typeEnum==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"参数不完整，请检查！");
        }
        YdPaypointCpdeviceInfo cpdeviceInfo = ydPaypointCpdeviceInfoMapper.selectByDeviceId(deviceid);
        if(cpdeviceInfo==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"设备不存在！");
        }
        if(times==null){
            times = 1;
        }
        if(times<=0){
            times = 1;
        }
        TypeEnum deviceType = TypeEnum.PRINTER;
        boolean needAddPrinter = false;
        if(StringUtil.equals(shopid,cpdeviceInfo.getShopid())&&
                StringUtil.equals(deviceType.getName(),cpdeviceInfo.getDeviceType())){
            if(StringUtil.isBlank(sn)||StringUtil.isBlank(key)) {
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "SN或者Key不能为空！");
            }
            sn = sn.trim()+"#"+key.trim();
            if(!StringUtil.equals(sn,cpdeviceInfo.getSn())){
                needAddPrinter = true;
            }

            YdPaypointCpdeviceInfo paypointCpdeviceInfo = ydPaypointCpdeviceInfoMapper.selectBySN(sn,deviceType.getName());
            if(paypointCpdeviceInfo!=null) {
                if (!StringUtil.equals(paypointCpdeviceInfo.getDeviceid(), cpdeviceInfo.getDeviceid())) {
                    throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "相同序号(SN)打印机已经存在，不能修改！");
                }
            }

            cpdeviceInfo.setSn(sn);
            if(StringUtil.isNotBlank(name)) {
                cpdeviceInfo.setName(name);
            }
            if(StringUtil.isNotBlank(description)) {
                cpdeviceInfo.setDescription(description);
            }
            CPDeviceInfoDTO deviceInfoDTO = doMapper.map(cpdeviceInfo,CPDeviceInfoDTO.class);
            if(PrintTypeEnum.nameOf(deviceInfoDTO.getFeature(Constant.PRINT_TYPE))!=typeEnum){
                deviceInfoDTO.removeFeature(Constant.PRINT_TYPE);
                deviceInfoDTO.addFeature(Constant.PRINT_TYPE,typeEnum.getName());
            }
            deviceInfoDTO.addFeature(Constant.PRINT_TIMES,times.toString());
            cpdeviceInfo.setFeature(deviceInfoDTO.getFeature());
            if(ydPaypointCpdeviceInfoMapper.updateByPrimaryKeySelective(cpdeviceInfo)<=0){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"修改设备信息失败!");
            }
            if(needAddPrinter) {
                List<String> printerInfo = new ArrayList<>();
                printerInfo.add(cpdeviceInfo.getSn() + "#" + cpdeviceInfo.getName() + "#");
                Map<String, Object> pResult = fePrinterService.printerAddlist(printerInfo);
                logger.info("modifyPrinter fePrinterService.printerAddlist result: " + JSON.toJSONString(pResult));
            }


        }else {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),ErrorCodeConstants.PARAM_ERROR.getErrorMessage());
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delDevice(String openid, String shopid, String deviceid) {
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        YdPaypointCpdeviceInfo paypointCpdeviceInfo = ydPaypointCpdeviceInfoMapper.selectByDeviceId(deviceid);
        if(!this.checkDeviceInShop(shopid,deviceid,TypeEnum.nameOf(paypointCpdeviceInfo.getDeviceType()))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdPaypointShopInfo shopInfo = ydPaypointShopInfoMapper.selectByShopIdRowLock(shopid);
        ShopInfoDTO shopInfoDTO = doMapper.map(shopInfo,ShopInfoDTO.class);
        shopInfoDTO.delDeviceValue(paypointCpdeviceInfo.getDeviceType(),deviceid);
        if(StringUtil.equals(deviceid,shopInfoDTO.getFeature(Constant.SHOP_PRINT))){
            shopInfoDTO.removeFeature(Constant.SHOP_PRINT);
        }
        shopInfo.setFeature(shopInfoDTO.getFeature());
        if(ydPaypointShopInfoMapper.updateByPrimaryKey(shopInfo)<=0){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorMessage(), "删除失败！");
        }
        if(ydPaypointCpdeviceInfoMapper.deleteByPrimaryKey(paypointCpdeviceInfo.getId())<=0){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorMessage(), "删除失败！");
        }
        return true;
    }

    @Override
    public Map<String, Object> batchBindPrinter(String openid, String shopid, String printerid, List<String> tables) {
        if(tables==null||tables.size()<=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"请选择桌位进行绑定!");
        }
        if(!(userinfoService.checkIsManager(openid,shopid)||(userinfoService.checkIsWaiter(openid,shopid)))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        PrinterInfoVO printerInfoVO = (PrinterInfoVO) this.queryDeviceInfo(openid,shopid,printerid,TypeEnum.PRINTER);
        if(printerInfoVO==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"当前店铺中无此打印机！");
        }
        Map<String, Object> result = new HashMap<>();
        PrintTypeEnum printTypeEnum = PrintTypeEnum.nameOf(printerInfoVO.getPrintType());
        if(printTypeEnum==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"打印机类型异常！");
        }
        result.put("printType",printerInfoVO.getPrintType());
        result.put("printName",printerInfoVO.getPrintTypeName());

        /**
         * 开始绑桌位
         */
        Integer successCount = 0;
        for(String tableid:tables){
            YdPaypointDiningtable paypointDiningtable = ydPaypointDiningtableMapper.selectByTableId(tableid);
            if(paypointDiningtable==null){
                continue;
            }
            if(!StringUtil.equals(paypointDiningtable.getShopid(),shopid)){
                continue;
            }
            DiningtableDTO diningtableDTO = doMapper.map(paypointDiningtable,DiningtableDTO.class);
            diningtableDTO.addFeature(printTypeEnum.getName(),printerid);
            paypointDiningtable.setFeature(diningtableDTO.getFeature());
            if(ydPaypointDiningtableMapper.updateByPrimaryKeySelective(paypointDiningtable)>0){
                successCount = successCount + 1;
            }

        }
        result.put("successCount",successCount);

        return result;
    }

    @Override
    public Object queryDeviceInfo(String openid, String shopid, String deviceid,TypeEnum type) {
        if(type==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"参数不完整，请检查！");
        }
        if(!(userinfoService.checkIsManager(openid,shopid)||(userinfoService.checkIsWaiter(openid,shopid)))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        if(!this.checkDeviceInShop(shopid,deviceid,type)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"当前店铺中无此设备！");
        }

        return this.queryDeviceInfo(shopid,deviceid,type);
    }

    @Override
    public Object queryDeviceInfo(String shopid, String deviceid, TypeEnum type) {
        YdPaypointCpdeviceInfo paypointCpdeviceInfo = ydPaypointCpdeviceInfoMapper.selectByDeviceId(deviceid);
        if(paypointCpdeviceInfo==null){
            logger.error("deviceid is not exits: " + deviceid);
            return null;
        }
        if(type==TypeEnum.PRINTER){

            CPDeviceInfoDTO deviceInfoDTO = doMapper.map(paypointCpdeviceInfo,CPDeviceInfoDTO.class);
            String printType = deviceInfoDTO.getFeature(Constant.PRINT_TYPE);
            PrinterInfoVO printerInfoVO = doMapper.map(paypointCpdeviceInfo,PrinterInfoVO.class);
            printerInfoVO.setPrintType(printType);
            printerInfoVO.setPrintTypeName(PrintTypeEnum.nameOf(printType).getDesc());
            String timesStr = deviceInfoDTO.getFeature(Constant.PRINT_TIMES);
            Integer times = 1;
            try{
                times = Integer.valueOf(timesStr);
            }catch (Exception ex){
                times = 1;
            }
            printerInfoVO.setTimes(times);
            if(StringUtil.isNotBlank(printerInfoVO.getSn())){
                String[] snAndKey = printerInfoVO.getSn().split("#");
                if(snAndKey!=null&&snAndKey.length==2) {
                    printerInfoVO.setSn(snAndKey[0]);
                    printerInfoVO.setKey(snAndKey[1]);
                }
            }
            return printerInfoVO;

        }else{
            CPDeviceInfoVO deviceInfoVO = doMapper.map(paypointCpdeviceInfo,CPDeviceInfoVO.class);
            return deviceInfoVO;
        }
    }

    @Override
    public List<Object> queryDeviceInfoList(String openid, String shopid,TypeEnum type,PrintTypeEnum printTypeEnum) {
        if(printTypeEnum==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"参数不完整，请检查！");
        }
        if(!(userinfoService.checkIsManager(openid,shopid)||(userinfoService.checkIsWaiter(openid,shopid)))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        YdPaypointShopInfo ydPaypointShopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
        if(ydPaypointShopInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"店铺不存在！");
        }
        ShopInfoDTO shopInfoDTO = doMapper.map(ydPaypointShopInfo,ShopInfoDTO.class);
        List<String> deviceIds = shopInfoDTO.getDeviceValue(type.getName());
        if(deviceIds==null||deviceIds.size()<=0){
            return new ArrayList<>();
        }
        List<Object> result = new ArrayList<>();
        if(printTypeEnum==null) {
            for (String deviceId : deviceIds) {
                result.add(this.queryDeviceInfo(openid, shopid, deviceId, type));
            }
        }else{
            if(type!=TypeEnum.PRINTER){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"设备类型不正确！");
            }
            for (String deviceId : deviceIds) {
                Object object = this.queryDeviceInfo(openid, shopid, deviceId, type);
                if(object==null){
                    continue;
                }
                PrinterInfoVO printerInfoVO = (PrinterInfoVO) object;
                if(printTypeEnum==PrintTypeEnum.nameOf(printerInfoVO.getPrintType())){
                    result.add(printerInfoVO);
                }

            }
        }

        return result;
    }

    @Override
    public List<Object> queryDeviceInfoList(String openid, String shopid, TypeEnum type, PrintTypeEnum printTypeEnum, SourceEnum sourceEnum) {
        if (sourceEnum==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"来源不能为空！");
        }

        if (!(SourceEnum.WEIXINAGENT.equals(sourceEnum)||SourceEnum.WEIXINXIAOER.equals(sourceEnum))){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"来源错误！");
        }

        if(printTypeEnum==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"参数不完整，请检查！");
        }

        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenid(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误!");
        }

        YdManageUserWhiteList ydManageUserWhiteList = ydManageUserWhiteListMapper.selectByMobile(ydManageUserInfo.getMobile());
        if (ydManageUserWhiteList==null){
            YdPaypointShopInfoExt ydPaypointShopInfoExt = ydPaypointShopInfoExtMapper.selectByShopId(shopid);
            if(ydPaypointShopInfoExt==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"店铺不存在！");
            }
            if (!StringUtil.equals(cpAgentService.getDefualtAgent(openid),ydPaypointShopInfoExt.getAgentid())){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"店铺不在代理商下！");
            }
        }


        YdPaypointShopInfo ydPaypointShopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
        if(ydPaypointShopInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"店铺不存在！");
        }
        ShopInfoDTO shopInfoDTO = doMapper.map(ydPaypointShopInfo,ShopInfoDTO.class);
        List<String> deviceIds = shopInfoDTO.getDeviceValue(type.getName());
        if(deviceIds==null||deviceIds.size()<=0){
            return new ArrayList<>();
        }
        List<Object> result = new ArrayList<>();
        if(printTypeEnum==null) {
            for (String deviceId : deviceIds) {
                result.add(this.queryDeviceInfo(openid, shopid, deviceId, type));
            }
        }else{
            if(type!=TypeEnum.PRINTER){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"设备类型不正确！");
            }
            for (String deviceId : deviceIds) {
                Object object = this.queryDeviceInfo(shopid, deviceId, type);
                if(object==null){
                    continue;
                }
                PrinterInfoVO printerInfoVO = (PrinterInfoVO) object;
                if(printTypeEnum==PrintTypeEnum.nameOf(printerInfoVO.getPrintType())){
                    result.add(printerInfoVO);
                }

            }
        }

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String addTable(String openid, DiningtableVO diningtableVO) throws Exception {
        if(diningtableVO==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"参数不完整，请检查！");
        }
        if(!userinfoService.checkIsManager(openid,diningtableVO.getShopid())){
            logger.error("diningtableVO is :"+JSON.toJSONString(diningtableVO));
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        if(diningtableVO.getName().length()>5){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "桌位号请不要超过5个字符！");
        }
        if(StringUtil.isNotBlank(diningtableVO.getQrcode())) {
            YdPaypointDiningtable diningtable = ydPaypointDiningtableMapper.selectByQrcode(diningtableVO.getQrcode());
            YdTableQrcode tableQrcode = ydTableQrcodeMapper.selectByQrcode(diningtableVO.getQrcode());
            if (diningtable != null || tableQrcode != null) {
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "扫描码已经存在！");
            }
        }else {
            diningtableVO.setQrcode(RandomUtil.getSNCode(TypeEnum.QRCODE));
        }
        if(StringUtil.isEmpty(diningtableVO.getTableid())) {
            diningtableVO.setTableid(RandomUtil.getSNCode(TypeEnum.DININGTABLE));
        }else {
            if(TypeEnum.getTypeOfSN(diningtableVO.getTableid())!=TypeEnum.DININGTABLE){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "桌位ID格式不正确!");
            }
        }


        YdPaypointDiningtable paypointDiningtable = doMapper.map(diningtableVO,YdPaypointDiningtable.class);
        DiningtableDTO diningtableDTO = doMapper.map(paypointDiningtable,DiningtableDTO.class);
        if(StringUtil.isNotBlank(diningtableVO.getKitchenPrintId())||StringUtil.isNotBlank(diningtableVO.getCashierPrintId())){
            if(StringUtil.isNotBlank(diningtableVO.getKitchenPrintId())){
                diningtableDTO.addFeature(PrintTypeEnum.KITCHEN.getName(),diningtableVO.getKitchenPrintId().trim());
            }
            if(StringUtil.isNotBlank(diningtableVO.getCashierPrintId())){
                diningtableDTO.addFeature(PrintTypeEnum.CASHIER.getName(),diningtableVO.getCashierPrintId().trim());
            }

        }

        /**
         * 生成小程序码
         */
        byte[] content = weixinSamll2ShopService.getWxAcodeUnlimit(diningtableVO.getShopid(),diningtableVO.getTableid(), DiamondWeiXinInfoConfigHolder.getInstance().getDefaultOnlinMainPage());
        if(content==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "生成小程序码失败，导致新增桌位失败!");
        }
        String qrcodeImageUrl = ossFileService.uploadWeiXinSmallImage(content,diningtableVO.getTableid());
        if(StringUtil.isEmpty(qrcodeImageUrl)){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "转码失败，导致新增桌位失败!");
        }

        diningtableDTO.addFeature(Constant.QRCodeUrl,qrcodeImageUrl);

        paypointDiningtable.setFeature(diningtableDTO.getFeature());

        if(ydPaypointDiningtableMapper.insert(paypointDiningtable)>0){
            /**
             * 初始4个qrcode与桌位绑定
             */
            this.addTableQrcode(diningtableVO.getShopid(),diningtableVO.getTableid(),diningtableVO.getQrcode());
            this.addTableQrcode(diningtableVO.getShopid(),diningtableVO.getTableid(),RandomUtil.getSNCode(TypeEnum.QRCODE));
            this.addTableQrcode(diningtableVO.getShopid(),diningtableVO.getTableid(),RandomUtil.getSNCode(TypeEnum.QRCODE));
            this.addTableQrcode(diningtableVO.getShopid(),diningtableVO.getTableid(),RandomUtil.getSNCode(TypeEnum.QRCODE));
            return diningtableVO.getTableid();
        }
        return null;
    }

    private String addTableQrcode(String shopid, String tableid,String qrcode) {
        if(StringUtil.isBlank(qrcode)){
            qrcode = RandomUtil.getSNCode(TypeEnum.QRCODE);
        }
        YdTableQrcode tableQrcode = new YdTableQrcode();
        tableQrcode.setQrcode(qrcode);
        tableQrcode.setShopid(shopid);
        tableQrcode.setTableid(tableid);
        if(ydTableQrcodeMapper.insert(tableQrcode)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),ErrorCodeConstants.YD10000.getErrorMessage());
        }
        return qrcode;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean modifyTable(String openid, DiningtableVO diningtableVO) throws Exception {
        if(diningtableVO==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(),"参数不完整，请检查！");
        }
        logger.info("diningtableVO qrcode is :" +diningtableVO.getQrcode());
        if(!userinfoService.checkIsManager(openid,diningtableVO.getShopid())){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        YdPaypointDiningtable diningtable = ydPaypointDiningtableMapper.selectByTableId(diningtableVO.getTableid());
        List<YdTableQrcode> tableQrcodes = ydTableQrcodeMapper.selectByTable(diningtableVO.getTableid());
        if (diningtable == null) {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "桌位不存在！");
        }

        if(!StringUtil.equals(diningtable.getShopid(),diningtableVO.getShopid())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "桌位不属于该店铺，不能修改！");
        }
        if(diningtableVO.getName().length()>5){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "桌位号请不要超过5个字符！");
        }
        if(StringUtil.isEmpty(diningtableVO.getQrcode())){
            diningtableVO.setQrcode(diningtable.getQrcode());
        }

        if(tableQrcodes!=null&&tableQrcodes.size()>0){
            tableQrcodes.get(0).setQrcode(diningtableVO.getQrcode());
        }else {
            /**
             * 初始4个qrcode与桌位绑定
             */
            this.addTableQrcode(diningtableVO.getShopid(),diningtableVO.getTableid(),diningtableVO.getQrcode());
            this.addTableQrcode(diningtableVO.getShopid(),diningtableVO.getTableid(),RandomUtil.getSNCode(TypeEnum.QRCODE));
            this.addTableQrcode(diningtableVO.getShopid(),diningtableVO.getTableid(),RandomUtil.getSNCode(TypeEnum.QRCODE));
            this.addTableQrcode(diningtableVO.getShopid(),diningtableVO.getTableid(),RandomUtil.getSNCode(TypeEnum.QRCODE));
        }
        if(StringUtil.isEmpty(diningtableVO.getQrcode())){
            diningtableVO.setQrcode(diningtable.getQrcode());
        }else{
            YdPaypointDiningtable paypointDiningtable = ydPaypointDiningtableMapper.selectByQrcode(diningtable.getQrcode());
            if(paypointDiningtable!=null) {
                if (!StringUtil.equals(paypointDiningtable.getTableid(), diningtable.getTableid())) {
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "扫描码已经存在！");
                }
            }
        }

        this.modifyQrcodeByModifyDiningTable(openid,tableQrcodes);

        if(StringUtil.isBlank(diningtableVO.getName())) {
            diningtableVO.setName(diningtable.getName());
        }

        YdPaypointDiningtable updateTableDO = doMapper.map(diningtableVO,YdPaypointDiningtable.class);
        updateTableDO.setId(diningtable.getId());

        DiningtableDTO diningtableDTO = doMapper.map(updateTableDO,DiningtableDTO.class);
        if(StringUtil.isEmpty(diningtableDTO.getFeature(Constant.QRCodeUrl))){
            /**
             * 生成小程序码
             */
            byte[] content = weixinSamll2ShopService.getWxAcodeUnlimit(diningtableVO.getShopid(),diningtableVO.getTableid(), DiamondWeiXinInfoConfigHolder.getInstance().getDefaultOnlinMainPage());
            if(content==null){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "生成小程序码失败，导致新增桌位失败!");
            }
            String qrcodeImageUrl = ossFileService.uploadWeiXinSmallImage(content,diningtableVO.getTableid());
            if(StringUtil.isEmpty(qrcodeImageUrl)){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "转码失败，导致新增桌位失败!");
            }

            diningtableDTO.addFeature(Constant.QRCodeUrl,qrcodeImageUrl);
        }
        if(StringUtil.isNotBlank(diningtableVO.getKitchenPrintId())||StringUtil.isNotBlank(diningtableVO.getCashierPrintId())){
            if(StringUtil.isNotBlank(diningtableVO.getKitchenPrintId())){
                diningtableDTO.addFeature(PrintTypeEnum.KITCHEN.getName(),diningtableVO.getKitchenPrintId().trim());
            }
            if(StringUtil.isNotBlank(diningtableVO.getCashierPrintId())){
                diningtableDTO.addFeature(PrintTypeEnum.CASHIER.getName(),diningtableVO.getCashierPrintId().trim());
            }

        }
        updateTableDO.setFeature(diningtableDTO.getFeature());

        if(ydPaypointDiningtableMapper.updateByPrimaryKeySelective(updateTableDO)>0){
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean modifyQrcode(String openid, List<YdTableQrcode> qrcodes) {
        if(qrcodes==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "二维码不能为空！");
        }
        if(qrcodes.size()!=4){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "二维码列表必须为4个！");
        }
        for(YdTableQrcode tableQrcode:qrcodes){
            YdTableQrcode oldQrcode = ydTableQrcodeMapper.selectByPrimaryKey(tableQrcode.getId());
            if(oldQrcode==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "id不存在！");
            }
            YdTableQrcode qrcodeRecod = ydTableQrcodeMapper.selectByQrcode(tableQrcode.getQrcode());
            if(qrcodeRecod!=null){
                if(qrcodeRecod.getId().longValue()!=tableQrcode.getId().longValue()){
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "存在重复的二维码!");
                }
            }
            if(!userinfoService.checkIsManager(openid,oldQrcode.getShopid())){
                throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
            }
            if(!StringUtil.equals(tableQrcode.getTableid(),oldQrcode.getTableid())){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "桌位id错误!");
            }

            oldQrcode.setQrcode(tableQrcode.getQrcode());
            if(ydTableQrcodeMapper.updateByPrimaryKeySelective(oldQrcode)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorMessage(),"保存二维码失败!");
            }

        }
        return true;
    }

    protected boolean modifyQrcodeByModifyDiningTable(String openid, List<YdTableQrcode> qrcodes) {
        if(qrcodes==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "二维码不能为空！");
        }
        if(qrcodes.size()!=4){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "二维码列表必须为4个！");
        }
        for(YdTableQrcode tableQrcode:qrcodes){
            YdTableQrcode oldQrcode = ydTableQrcodeMapper.selectByPrimaryKey(tableQrcode.getId());
            if(oldQrcode==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "id不存在！");
            }
            YdTableQrcode qrcodeRecod = ydTableQrcodeMapper.selectByQrcode(tableQrcode.getQrcode());
            if(qrcodeRecod!=null){
                if(qrcodeRecod.getId().longValue()!=tableQrcode.getId().longValue()){
                    throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "存在重复的二维码!");
                }
            }
            if(!userinfoService.checkIsManager(openid,oldQrcode.getShopid())){
                throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
            }
            if(!StringUtil.equals(tableQrcode.getTableid(),oldQrcode.getTableid())){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "桌位id错误!");
            }

            oldQrcode.setQrcode(tableQrcode.getQrcode());
            if(ydTableQrcodeMapper.updateByPrimaryKeySelective(oldQrcode)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorMessage(),"保存二维码失败!");
            }

        }
        return true;
    }

    @Override
    public boolean delTable(String openid, String shopid, String tableid) {
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdPaypointDiningtable diningtable = ydPaypointDiningtableMapper.selectByTableId(tableid);
        if (diningtable == null) {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "桌位不存在！");
        }
        if(!StringUtil.equals(diningtable.getShopid(),shopid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "桌位不属于该店铺，不能删除！");
        }
        ydPaypointDiningtableMapper.deleteByPrimaryKey(diningtable.getId());
        return true;
    }


    @Override
    public DiningtableVO queryTableByTableId(String shopid, String tableid) {
        YdPaypointDiningtable diningtable = ydPaypointDiningtableMapper.selectByTableId(tableid);
        if (diningtable == null) {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "桌位不存在！");
        }
        if(!StringUtil.equals(diningtable.getShopid(),shopid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "桌位不存在！");
        }
        DiningtableVO diningtableVO = doMapper.map(diningtable,DiningtableVO.class);
        DiningtableDTO diningtableDTO = doMapper.map(diningtable,DiningtableDTO.class);
        diningtableVO.setQrcode(diningtableDTO.getFeature(Constant.QRCodeUrl));
        String kitchenPrintId = diningtableDTO.getFeature(PrintTypeEnum.KITCHEN.getName());
        String cashierPrintId = diningtableDTO.getFeature(PrintTypeEnum.CASHIER.getName());
        if(StringUtil.isNotBlank(kitchenPrintId)){
            YdPaypointCpdeviceInfo paypointCpdeviceInfo = ydPaypointCpdeviceInfoMapper.selectByDeviceId(kitchenPrintId);
            if(paypointCpdeviceInfo!=null) {
                CPDeviceInfoDTO deviceInfoDTO = doMapper.map(paypointCpdeviceInfo,CPDeviceInfoDTO.class);
                if(PrintTypeEnum.nameOf(deviceInfoDTO.getFeature(Constant.PRINT_TYPE))==PrintTypeEnum.KITCHEN){
                    diningtableVO.setKitchenPrintId(kitchenPrintId);
                }
            }
        }
        if(StringUtil.isNotBlank(cashierPrintId)){
            YdPaypointCpdeviceInfo paypointCpdeviceInfo = ydPaypointCpdeviceInfoMapper.selectByDeviceId(cashierPrintId);
            if(paypointCpdeviceInfo!=null) {
                CPDeviceInfoDTO deviceInfoDTO = doMapper.map(paypointCpdeviceInfo,CPDeviceInfoDTO.class);
                if(PrintTypeEnum.nameOf(deviceInfoDTO.getFeature(Constant.PRINT_TYPE))==PrintTypeEnum.CASHIER){
                    diningtableVO.setCashierPrintId(cashierPrintId);
                }
            }

        }
        return diningtableVO;
    }

    @Override
    public DiningtableVO queryTableByQrcode(String qrcode) {
        YdPaypointDiningtable diningtable = ydPaypointDiningtableMapper.selectByQrcode(qrcode);
        YdTableQrcode tableQrcode = ydTableQrcodeMapper.selectByQrcode(qrcode);
        if(tableQrcode!=null&&diningtable==null){
            diningtable = ydPaypointDiningtableMapper.selectByTableId(tableQrcode.getTableid());
        }
        if (diningtable == null) {
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorMessage(), "扫描码不存在！");
        }
        DiningtableVO diningtableVO = doMapper.map(diningtable,DiningtableVO.class);
        DiningtableDTO diningtableDTO = doMapper.map(diningtable,DiningtableDTO.class);
        String kitchenPrintId = diningtableDTO.getFeature(PrintTypeEnum.KITCHEN.getName());
        String cashierPrintId = diningtableDTO.getFeature(PrintTypeEnum.CASHIER.getName());
        if(StringUtil.isNotBlank(kitchenPrintId)){
            YdPaypointCpdeviceInfo paypointCpdeviceInfo = ydPaypointCpdeviceInfoMapper.selectByDeviceId(kitchenPrintId);
            if(paypointCpdeviceInfo!=null) {
                CPDeviceInfoDTO deviceInfoDTO = doMapper.map(paypointCpdeviceInfo,CPDeviceInfoDTO.class);
                if(PrintTypeEnum.nameOf(deviceInfoDTO.getFeature(Constant.PRINT_TYPE))==PrintTypeEnum.KITCHEN){
                    diningtableVO.setKitchenPrintId(kitchenPrintId);
                }
            }
        }
        if(StringUtil.isNotBlank(cashierPrintId)){
            YdPaypointCpdeviceInfo paypointCpdeviceInfo = ydPaypointCpdeviceInfoMapper.selectByDeviceId(cashierPrintId);
            if(paypointCpdeviceInfo!=null) {
                CPDeviceInfoDTO deviceInfoDTO = doMapper.map(paypointCpdeviceInfo,CPDeviceInfoDTO.class);
                if(PrintTypeEnum.nameOf(deviceInfoDTO.getFeature(Constant.PRINT_TYPE))==PrintTypeEnum.CASHIER){
                    diningtableVO.setCashierPrintId(cashierPrintId);
                }
            }

        }
        return diningtableVO;
    }

    @Override
    public List<YdTableQrcode> getQrcodes(String openid,String tableid) {
        List<YdTableQrcode> tableQrcodes = ydTableQrcodeMapper.selectByTable(tableid);
        if(tableQrcodes!=null&&tableQrcodes.size()>0){
            String shopid = tableQrcodes.get(0).getShopid();
            if(!(userinfoService.checkIsManager(openid,shopid)||userinfoService.checkIsWaiter(openid,shopid))){
                throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
            }
        }
        return tableQrcodes;
    }

    @Override
    public List<DiningtableVO> queryTableByShopId(String openid, String shopid, Integer pageIndex, Integer count) {
        if(!(userinfoService.checkIsManager(openid,shopid)||userinfoService.checkIsWaiter(openid,shopid))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        Integer indexPoint = (pageIndex-1)*count;
        if(indexPoint<=0){
            indexPoint = 0;
        }
        List<YdPaypointDiningtable> diningtableList = ydPaypointDiningtableMapper.selectByShopId(shopid,indexPoint,count);
        List<DiningtableVO> diningtableVOS = new ArrayList<>();
        if(diningtableList==null||diningtableList.size()<=0){
            return diningtableVOS;
        }
        for(YdPaypointDiningtable diningtable:diningtableList){
            DiningtableVO diningtableVO = doMapper.map(diningtable,DiningtableVO.class);
            DiningtableDTO diningtableDTO = doMapper.map(diningtable,DiningtableDTO.class);
            String kitchenPrintId = diningtableDTO.getFeature(PrintTypeEnum.KITCHEN.getName());
            String cashierPrintId = diningtableDTO.getFeature(PrintTypeEnum.CASHIER.getName());
            if(StringUtil.isNotBlank(kitchenPrintId)){
                YdPaypointCpdeviceInfo paypointCpdeviceInfo = ydPaypointCpdeviceInfoMapper.selectByDeviceId(kitchenPrintId);
                if(paypointCpdeviceInfo!=null) {
                    CPDeviceInfoDTO deviceInfoDTO = doMapper.map(paypointCpdeviceInfo,CPDeviceInfoDTO.class);
                    if(PrintTypeEnum.nameOf(deviceInfoDTO.getFeature(Constant.PRINT_TYPE))==PrintTypeEnum.KITCHEN){
                        diningtableVO.setKitchenPrintId(kitchenPrintId);
                    }
                }
            }
            if(StringUtil.isNotBlank(cashierPrintId)){
                YdPaypointCpdeviceInfo paypointCpdeviceInfo = ydPaypointCpdeviceInfoMapper.selectByDeviceId(cashierPrintId);
                if(paypointCpdeviceInfo!=null) {
                    CPDeviceInfoDTO deviceInfoDTO = doMapper.map(paypointCpdeviceInfo,CPDeviceInfoDTO.class);
                    if(PrintTypeEnum.nameOf(deviceInfoDTO.getFeature(Constant.PRINT_TYPE))==PrintTypeEnum.CASHIER){
                        diningtableVO.setCashierPrintId(cashierPrintId);
                    }
                }

            }
            diningtableVOS.add(diningtableVO);
        }
        return diningtableVOS;
    }

    @Override
    public String getRandomQrCode(String openid, String shopid, Integer codeType) {
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        if(codeType==null){
            codeType = 8;
        }
        TypeEnum typeEnum = TypeEnum.nameOf(codeType);
        if(typeEnum==null){
            return null;
        }
        return RandomUtil.getSNCode(typeEnum);
    }

    @Override
    public ContractInfoVO getContractInfo(String contractId) {
        YdPaypointContractinfo contractinfo = ydPaypointContractinfoMapper.selectByContractId(contractId);
        if(contractinfo==null){
            return null;
        }
        return doMapper.map(contractinfo,ContractInfoVO.class);
    }

    @Override
    public List<UserContractVO> getUserContractInfo(String openid, String shopid) {
        if(!userinfoService.checkIsOwner(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        List<UserContractVO> result = new ArrayList<>();
        List<YdPaypointUserContract> userContract = ydPaypointUserContractMapper.selectByOpenid(openid);
        if(userContract==null||userContract.size()<=0){
            return result;
        }
        for(YdPaypointUserContract ydPaypointUserContract : userContract){
            if(StringUtil.equals(shopid,ydPaypointUserContract.getShopid())) {
                UserContractVO contractVO = doMapper.map(ydPaypointUserContract, UserContractVO.class);
                result.add(contractVO);
            }
        }
        return result;
    }

    @Override
    public ShopMainPageVO getShopMainPageData(String openid, String shopid) {
        if(!(userinfoService.checkIsManager(openid,shopid)||userinfoService.checkIsWaiter(openid,shopid))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        ShopMainPageVO shopMainPageVO = new ShopMainPageVO();
        YdPaypointShopInfo shopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
        if(shopInfo==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "店铺不存在!");
        }
        shopMainPageVO.setStatus(shopInfo.getStatus());
        ShopInfoDTO shopInfoDTO = doMapper.map(shopInfo,ShopInfoDTO.class);
        Integer orderCountToday = DiamondYdPayPointConfigHolder.getInstance().getOrderCountToday(shopInfoDTO.getFeature(Constant.SHOP_SETMEAL_TYPE));
        YdShoporderStatistics shoporderStatistics = ydShoporderStatisticsMapper.selectByShopid(shopid,new Date());
        if(shoporderStatistics==null){
            shopMainPageVO.setRemainder(orderCountToday);
            shopMainPageVO.setOrderCount(0);
            shopMainPageVO.setReceiveAmount(new BigDecimal("0.00"));
            return shopMainPageVO;
        }
        /**
         * 取今日数据
         */
        Integer bagCount = shoporderStatistics.getBagCount();
        shopMainPageVO.setOrderCount(shoporderStatistics.getOrderCount());
        String curShopType = shopInfoDTO.getFeature(Constant.SHOP_SETMEAL_TYPE);
        if(StringUtil.isBlank(curShopType)){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"店铺没有选择购买套餐！");
        }
        curShopType = curShopType.trim();
        if(!StringUtil.equals(curShopType,Constant.DIAMOND_VERSION)) {
            shopMainPageVO.setRemainder((orderCountToday + shoporderStatistics.getBagCount()) - shoporderStatistics.getOrderCount());
        }else{
            shopMainPageVO.setRemainder(orderCountToday);
        }
        shopMainPageVO.setReceiveAmount(shoporderStatistics.getReceiveAmount().add(shoporderStatistics.getReceivecashAmount()));
        return shopMainPageVO;
    }

    @Override
    public List<Order2CVO> getOrder2CByShop(String openid, String shopid, Date queryDate, String tableid,Integer pageIndex, Integer count) {
        if(!(userinfoService.checkIsManager(openid,shopid)||userinfoService.checkIsWaiter(openid,shopid))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        if(queryDate==null){
            queryDate = new Date();
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
        YdShoporderStatistics shoporderStatistics = ydShoporderStatisticsMapper.selectByShopid(shopid,queryDate);
        if(shoporderStatistics==null){
            return order2CVOList;
        }

        List<YdConsumerOrder> consumerOrderList = null;
        if(StringUtil.isBlank(tableid)){
            consumerOrderList = ydConsumerOrderMapper.selectByOrderManager(shoporderStatistics.getFirstid(),
                    shoporderStatistics.getLastid(),shopid,indexPoint,count);

        }else {
            consumerOrderList = ydConsumerOrderMapper.selectByOrderManagerHaveTableId(shoporderStatistics.getFirstid(),
                    shoporderStatistics.getLastid(),shopid,tableid,indexPoint,count);
        }
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

    @Override
    public Order2CVO getOrder2CDetailByShop(String openid,String shopid, String orderid) {
        if(!(userinfoService.checkIsManager(openid,shopid)||userinfoService.checkIsWaiter(openid,shopid))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdConsumerOrder order = ydConsumerOrderMapper.selectByOrderid(orderid);
        if(order==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"订单不存在！");
        }
        if(!StringUtil.equals(shopid,order.getShopid())){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"店铺无此订单！");
        }
        return this.doMapOrder(order);
    }

    @Override
    public Order2CVO calculateOrder2C(String openid, Order2CVO order2CVO) {
        if(order2CVO == null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),ErrorCodeConstants.PARAM_ERROR.getErrorMessage());
        }
        Map<String , ShoppingCartSkuVO> shoppingCartSkuVOMap = order2CVO.getShoppingCartSkuVOMap();
        if(shoppingCartSkuVOMap==null||shoppingCartSkuVOMap.keySet().isEmpty()){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"不能一个商品都没有呀!");
        }

        order2CVO.setTotalAmount(new BigDecimal("0.00"));
        order2CVO.setDisTotalAmount(new BigDecimal("0.00"));
        for(String skuid:shoppingCartSkuVOMap.keySet()){
            ShoppingCartSkuVO cartSkuVO = shoppingCartSkuVOMap.get(skuid);
            if(cartSkuVO==null){
                continue;
            }
            YdPaypointWaresSku waresSku = ydPaypointWaresSkuMapper.selectBySkuId(skuid);
            if(waresSku==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),cartSkuVO.getSkuName()+"在商品库中不存在！");
            }
            cartSkuVO.setPrice(AmountUtils.bigDecimalBy2(waresSku.getPrice()));
            cartSkuVO.setDisprice(AmountUtils.bigDecimalBy2(waresSku.getDisprice()));
            if(cartSkuVO.getCount()<0){
                cartSkuVO.setCount(0);
            }
            cartSkuVO.setTotalAmount(AmountUtils.bigDecimalBy2(AmountUtils.mul(cartSkuVO.getPrice(),cartSkuVO.getCount())));
            cartSkuVO.setDisTotalAmount(AmountUtils.bigDecimalBy2(AmountUtils.mul(cartSkuVO.getDisprice(),cartSkuVO.getCount())));
            order2CVO.setTotalAmount(AmountUtils.bigDecimalBy2(order2CVO.getTotalAmount().add(cartSkuVO.getTotalAmount())));
            order2CVO.setDisTotalAmount(order2CVO.getDisTotalAmount().add(cartSkuVO.getDisTotalAmount()));
        }

        boolean isDisCount = false;
        if(order2CVO.getDiscount()<0||order2CVO.getDiscount()>=1){
            order2CVO.setDiscount(1.0);
        }
        if(order2CVO.getDiscount()>0&&order2CVO.getDiscount()<1){
            isDisCount = true;
        }
        if(isDisCount){
            order2CVO.setTotalAmount(order2CVO.getDisTotalAmount());
            order2CVO.setDisTotalAmount(AmountUtils.mul(order2CVO.getDisTotalAmount(),order2CVO.getDiscount()));
        }


        return order2CVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean modifyOrder2C(String openid, Order2CVO order2CVO) {

        if(order2CVO == null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),ErrorCodeConstants.PARAM_ERROR.getErrorMessage());
        }
        if(!userinfoService.checkIsManager(openid,order2CVO.getShopid())){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        /**
         * 查操作员信息
         */
        Object object = userinfoService.getCpUserInfo(openid,order2CVO.getShopid());
        if(object==null){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),"操作员信息不存在！");
        }
        UserInfoVO cpUserInfo = (UserInfoVO)object;
        Order2CVO newOrder2C = this.calculateOrder2C(openid,order2CVO);

        /**
         * 查出原始订单信息
         */

        YdConsumerOrder consumerOrder = ydConsumerOrderMapper.selectByOrderidRowLock(newOrder2C.getOrderid());
        if(consumerOrder==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"订单不存在！");
        }
        if(consumerOrder.getStatus()>0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"订单状态不正确！");
        }
        if(!StringUtil.equals(consumerOrder.getShopid(),order2CVO.getShopid())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"订单的店铺信息不一致!");
        }

        /**
         * 因为这种订单可能在三方支付那里已经创建了一个支付订单，所以修改后的订单需要改变我们的商户订单号
         */
        newOrder2C.setOrderid(RandomUtil.getSNCode(TypeEnum.CONSUMERORDER));
        consumerOrder.setOrderid(newOrder2C.getOrderid());
        /**
         * 记录操作人员日志
         */
        if(consumerOrder.getManagerOptionHistoryList()!=null){
            if(consumerOrder.getManagerOptionHistoryList().size()>=3){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"同一个订单最多只能修改3次!");
            }
        }

        String managerOptionInfo = "操作员:"+cpUserInfo.getMobile()+"于"+ DateUtils.date2String(new Date())+"进行了订单金额修改操作";

        if(order2CVO.getDiscount()>0.0&&order2CVO.getDiscount()<1.0){
            consumerOrder.setManagerOptionHistory(managerOptionInfo+",打折"+order2CVO.getDiscount()+"的操作!");
        }else {
            consumerOrder.setManagerOptionHistory(managerOptionInfo);
        }


        /**
         * 更新折后订单信息
         */
        consumerOrder.setTotalAmount(newOrder2C.getTotalAmount());
        consumerOrder.setDisTotalAmount(newOrder2C.getDisTotalAmount());
        if(StringUtil.isBlank(consumerOrder.getHistory())){
            /**
             * 说明是第一次进行订单修改，需要将原始订单放入history字段
             */
            consumerOrder.setHistory(JSON.toJSONString(consumerOrder.getFeature()));
        }
//        Order2CVO oldOrder2C = this.doMapOrder(consumerOrder);
//        oldOrder2C.setShoppingCartSkuVOMap(newOrder2C.getShoppingCartSkuVOMap());
        if(AmountUtils.changeY2F(newOrder2C.getDisTotalAmount())<=0){
            /**
             * 说明免单了，订单状态直接到已经付款
             */
            consumerOrder.setStatus(OrderStatusEnum.PAYFINISH.getStatus());
        }
        consumerOrder.setFeature(JSON.toJSONString((ShoppingCartVO)newOrder2C));
        /**
         * 取一个商品名称作为订单名称前缀
         */

        Map<String, ShoppingCartSkuVO> cartSkuVOMap = newOrder2C.getShoppingCartSkuVOMap();
        Integer wareCount = cartSkuVOMap.keySet().size();
        String wareName = "";


        for(String key:cartSkuVOMap.keySet()){
            ShoppingCartSkuVO cartSkuVO = cartSkuVOMap.get(key);
            wareName = cartSkuVO.getSkuName();
            break;
        }

        wareName = StringUtil.abbreviate(wareName,10)+"等"+wareCount.toString()+"种菜品";
        newOrder2C.setOrderName(wareName);
        consumerOrder.setOrderName(wareName);

        /**
         * 更新数据库
         */
        if(ydConsumerOrderMapper.updateByPrimaryKeySelective(consumerOrder)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"修改订单失败!");
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean cancelOrder2C(String openid, String shopid, String orderid) {
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdConsumerOrder consumerOrder = ydConsumerOrderMapper.selectByOrderidRowLock(orderid);
        if(consumerOrder == null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"订单不存在!");
        }
        if(!StringUtil.equals(consumerOrder.getShopid(),shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        if(consumerOrder.getStatus().intValue()!=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"订单状态不正确!");
        }
        /**
         * 查操作员信息
         */
        Object object = userinfoService.getCpUserInfo(openid,shopid);
        if(object==null){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),"操作员信息不存在！");
        }
        UserInfoVO cpUserInfo = (UserInfoVO)object;

        consumerOrder.setStatus(OrderStatusEnum.OVER.getStatus());
        consumerOrder.setManagerOptionHistory("操作员:"+cpUserInfo.getMobile()+"于"+ DateUtils.date2String(new Date())+"取消了订单！");

        /**
         * 更新数据库
         */
        if(ydConsumerOrderMapper.updateByPrimaryKeySelective(consumerOrder)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"修改订单失败!");
        }

        return true;
    }



    @Override
    public boolean useCashPayWithOrder2C(String openid, String shopid, String orderid) {
        if(!(userinfoService.checkIsManager(openid,shopid)||
                userinfoService.checkIsWaiter(openid,shopid)||
                userinfoService.checkIsOwner(openid,shopid))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdConsumerOrder consumerOrder = ydConsumerOrderMapper.selectByOrderidRowLock(orderid);
        if(consumerOrder == null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"订单不存在!");
        }
        if(!StringUtil.equals(consumerOrder.getShopid(),shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        if(consumerOrder.getStatus()!=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"订单状态不正确!");
        }
        /**
         * 查操作员信息
         */
        Object object = userinfoService.getCpUserInfo(openid,shopid);
        if(object==null){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),"操作员信息不存在！");
        }
        UserInfoVO cpUserInfo = (UserInfoVO)object;

        consumerOrder.setUseCash(1);
        consumerOrder.setManagerOptionHistory("操作员:"+cpUserInfo.getMobile()+"于"+ DateUtils.date2String(new Date())+"确认了现金支付！");

        /**
         * 更新数据库
         */
        if(ydConsumerOrderMapper.updateByPrimaryKeySelective(consumerOrder)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"修改订单失败!");
        }

        /**
         * 更新成功，发消息改订单状态
         */
        Map<String,String> paySuccessMessage = new HashMap<>();
        paySuccessMessage.put(Constant.ORDERID,consumerOrder.getOrderid());
        paySuccessMessage.put(Constant.PAYORDERTYPE, PayOrderTypeEnum.nameOf(consumerOrder.getOrderType()).getName());
        paySuccessMessage.put(Constant.ORDERTYPE, OrderStatusEnum.PAYCASH.getName());
        mqMessageService.sendYdPayMessage(consumerOrder.getOrderid(),JSON.toJSONString(paySuccessMessage));

        return true;
    }

    @Override
    public BankInfoVO getBankInfo(String openid, String shopid) {
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdPaypointBankInfo ydPaypointBankInfo = ydPaypointBankInfoMapper.selectByOpenidAndShopid(openid,shopid);
        if(ydPaypointBankInfo==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"银行卡信息不存在!");
        }
        BankInfoVO bankInfoVO = doMapper.map(ydPaypointBankInfo,BankInfoVO.class);
        return bankInfoVO;
    }

    @Override
    public boolean saveBankInfo(String openid, BankInfoVO bankInfoVO) {
        if(!userinfoService.checkIsManager(openid,bankInfoVO.getShopid())){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        if(StringUtil.isBlank(bankInfoVO.getBankAccountCode())||StringUtil.isBlank(bankInfoVO.getBankAccountName())||
                StringUtil.isBlank(bankInfoVO.getBankName())||StringUtil.isBlank(bankInfoVO.getCity())||
                StringUtil.isBlank(bankInfoVO.getProvince())||StringUtil.isBlank(bankInfoVO.getIdentityCard())||
                StringUtil.isBlank(bankInfoVO.getSubbranchName())||StringUtil.isBlank(bankInfoVO.getMobile())||
                StringUtil.isBlank(bankInfoVO.getYeepayCityCode())||StringUtil.isBlank(bankInfoVO.getYeepayBankCode())||
                StringUtil.isBlank(bankInfoVO.getBusinessLicenceImg())){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"信息填写不完整!");
        }
        if(bankInfoVO.getCartType().intValue()==0){
            if(StringUtil.isBlank(bankInfoVO.getIdentityFrontImg())||StringUtil.isBlank(bankInfoVO.getIdentityBackImg())){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"身份证图片需要上传!");
            }
        }else {
            if(StringUtil.isBlank(bankInfoVO.getBankAccountLicenceImg())){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"对公账户开户许可证图片需要上传!");
            }
        }
        YdPaypointBankInfo ydPaypointBankInfo = ydPaypointBankInfoMapper.selectByOpenidAndShopid(openid,bankInfoVO.getShopid());
        if(ydPaypointBankInfo==null){
            /**
             * 新增
             */
            ydPaypointBankInfo = doMapper.map(bankInfoVO,YdPaypointBankInfo.class);
            ydPaypointBankInfo.setOpenid(openid);
            ydPaypointBankInfo.setStatus(1);
            if(ydPaypointBankInfoMapper.insert(ydPaypointBankInfo)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"银行卡信息增加失败!");
            }
        }else {
            /**
             * 修改
             */
            if(ydPaypointBankInfo.getStatus().intValue()!=0){
                /**
                 * 只有在为0时才能修改
                 */
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"当前状态不能修改!");
            }
            YdPaypointBankInfo ydPaypointBankInfoUpdate = doMapper.map(bankInfoVO,YdPaypointBankInfo.class);
            ydPaypointBankInfoUpdate.setOpenid(openid);
            ydPaypointBankInfoUpdate.setId(ydPaypointBankInfo.getId());
            ydPaypointBankInfoUpdate.setStatus(1);
            if(ydPaypointBankInfoMapper.updateByPrimaryKeySelective(ydPaypointBankInfoUpdate)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"银行卡信息修改失败!");
            }
        }
        return true;
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


    protected boolean checkDeviceInShop(String shopid,String deviceid, TypeEnum type) {
        if(StringUtil.isBlank(shopid)||StringUtil.isBlank(deviceid)||type==null){
            return false;
        }
        YdPaypointShopInfo shopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
        if(shopInfo==null){
            return false;
        }
        ShopInfoDTO shopInfoDTO = doMapper.map(shopInfo,ShopInfoDTO.class);
        List<String> deviceList = shopInfoDTO.getDeviceValue(type.getName());
        if(deviceList==null||deviceList.size()<=0){
            return false;
        }
        return deviceList.contains(deviceid);
    }

    /**
     * ------------------------------------独立的商城模式-------------------------------------------
     */

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean modifyUserOrder2C(String openid, ShopOrder2C shopOrder2C) {
        boolean isOwner = userinfoService.checkIsOwner(openid,shopOrder2C.getShopid());
        if(!(isOwner||
                userinfoService.checkIsManager(openid,shopOrder2C.getShopid()))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        if(shopOrder2C.getShoppingOrderSkuVOList()==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商品列表不能为空！");
        }

        if(shopOrder2C.getDiscount()==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "缺少折扣信息！");
        }

        YdShopConsumerOrder shopConsumerOrder = ydShopConsumerOrderMapper.selectByOrderidRowLock(shopOrder2C.getOrderid());

        if(!StringUtil.equals(shopOrder2C.getShopid(),shopConsumerOrder.getShopid())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"店铺信息不一致！");
        }

        if(shopConsumerOrder==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"订单不存在！");
        }

        ShopOrder2C oldOrder2C = JSON.parseObject(shopConsumerOrder.getFeature(),ShopOrder2C.class);

        /**
         * 查操作员信息
         */
        Object object = userinfoService.getCpUserInfo(openid,shopOrder2C.getShopid());
        if(object==null){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),"操作员信息不存在！");
        }
        UserInfoVO cpUserInfo = (UserInfoVO)object;

        /**
         * 保存历史信息
         */
        shopConsumerOrder.setHistory(shopConsumerOrder.getFeature());
        /**
         * 开始计算金额
         */
        ShopOrder2C newOrder2C = JSON.parseObject(shopConsumerOrder.getFeature(),ShopOrder2C.class);
        newOrder2C.setShoppingOrderSkuVOList(shopOrder2C.getShoppingOrderSkuVOList());
        newOrder2C.setDiscount(shopOrder2C.getDiscount());
        newOrder2C.setDescription(shopOrder2C.getDescription());
        newOrder2C = this.calculateUserOrder2C(newOrder2C);
        shopConsumerOrder.setFeature(JSON.toJSONString(newOrder2C));
        shopConsumerOrder.setStatus(newOrder2C.getStatus());
        shopConsumerOrder.setTotalAmount(newOrder2C.getDisTotalAmount());

        /**
         * 记录操作人员日志
         */
        if(shopConsumerOrder.getManagerOptionHistoryList()!=null){
            if(shopConsumerOrder.getManagerOptionHistoryList().size()>=3){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"同一个订单最多只能修改3次!");
            }
        }

        String lastMobile = StringUtil.substring(cpUserInfo.getMobile(),cpUserInfo.getMobile().length()-5);
        String managerOptionInfo = "手机尾号为" + lastMobile + "的操作员:" + cpUserInfo.getNick() + "于" + DateUtils.date2String(new Date()) + "进行了订单金额修改操作";

        if(oldOrder2C.getDiscount().doubleValue()!=newOrder2C.getDiscount().doubleValue()){
            shopConsumerOrder.setManagerOptionHistory(managerOptionInfo+",打折"+shopOrder2C.getDiscount()+"的操作!");
        }else {
            shopConsumerOrder.setManagerOptionHistory(managerOptionInfo);
        }

        /**
         * 入库更新
         */

        if(ydShopConsumerOrderMapper.updateByPrimaryKeySelective(shopConsumerOrder)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"订单修改失败！");
        }

        this.updateOrder2OpenSearch(shopConsumerOrder.getOrderid());

        return true;
    }

    protected ShopOrder2C calculateUserOrder2C(ShopOrder2C shopOrder2C){

        List<ShoppingOrderSkuVO> shoppingOrderSkuVOS = shopOrder2C.getShoppingOrderSkuVOList();

        if(shoppingOrderSkuVOS==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商品列表不能为空！");
        }

        shopOrder2C.setTotalAmount(new BigDecimal("0.00"));
        shopOrder2C.setDisTotalAmount(new BigDecimal("0.00"));

        for(ShoppingOrderSkuVO orderSkuVO : shoppingOrderSkuVOS){
            YdPaypointWaresSku waresSku = ydPaypointWaresSkuMapper.selectBySkuId(orderSkuVO.getSkuid());
            if(waresSku==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), orderSkuVO.getSkuName()+"-商品信息不存在!");
            }
            if(!StringUtil.equals(waresSku.getShopid(),shopOrder2C.getShopid())){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), waresSku.getName()+"-不是此店铺的商品!");
            }
            orderSkuVO.setSkuImgUrl(waresSku.getWareimg());

            Map<String,Object> skuPrice = specConfigService.getPriceBySku(orderSkuVO.getSkuid(),orderSkuVO.getMainSpecName(),orderSkuVO.getChildSpecName());
            if(skuPrice==null){
                throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), orderSkuVO.getSkuName()+"-价格查询失败!");
            }

            orderSkuVO.setPrice((BigDecimal) skuPrice.get("price"));
            orderSkuVO.setDisprice((BigDecimal)skuPrice.get("disPrice"));

            /**
             * 计算单一订单总金额
             */
            orderSkuVO.setTotalAmount(AmountUtils.bigDecimalBy2(AmountUtils.mul(orderSkuVO.getPrice(),orderSkuVO.getCount())));
            orderSkuVO.setDisTotalAmount(AmountUtils.bigDecimalBy2(AmountUtils.mul(orderSkuVO.getDisprice(),orderSkuVO.getCount())));
            /**
             * 计算所有订单总金额
             */
            shopOrder2C.setTotalAmount(AmountUtils.bigDecimalBy2(AmountUtils.add(shopOrder2C.getTotalAmount(),orderSkuVO.getTotalAmount())));
            shopOrder2C.setDisTotalAmount(AmountUtils.bigDecimalBy2(AmountUtils.add(shopOrder2C.getDisTotalAmount(),orderSkuVO.getDisTotalAmount())));

        }
        shopOrder2C.setDisTotalAmount(AmountUtils.bigDecimalBy2(AmountUtils.mul(shopOrder2C.getDisTotalAmount(),shopOrder2C.getDiscount())));

        return shopOrder2C;
    }

    @Override
    public ShopOrder2C calculateUserOrder2C(String openid, ShopOrder2C shopOrder2C) {

        if(!(userinfoService.checkIsManager(openid,shopOrder2C.getShopid())||userinfoService.checkIsWaiter(openid,shopOrder2C.getShopid()))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        if(shopOrder2C.getShoppingOrderSkuVOList()==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商品列表不能为空！");
        }

        ShopOrder2C newOrder2C = this.calculateUserOrder2C(shopOrder2C);
        return newOrder2C;
    }



    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean cancelUserOrder2C(String openid, String shopid, String orderid) {
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdShopConsumerOrder consumerOrder = ydShopConsumerOrderMapper.selectByOrderidRowLock(orderid);
        if(consumerOrder==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"订单不存在!");
        }
        if(!StringUtil.equals(consumerOrder.getShopid(),shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        if((consumerOrder.getStatus().intValue()<0)||(consumerOrder.getStatus().intValue()>=3)||(consumerOrder.getIsPay().intValue()>=0)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"当前状态不能被取消!");
        }

        /**
         * 查操作员信息
         */
        Object object = userinfoService.getCpUserInfo(openid,shopid);
        if(object==null){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),"操作员信息不存在！");
        }
        UserInfoVO cpUserInfo = (UserInfoVO)object;

        String managerOptionInfo = "操作员:"+cpUserInfo.getMobile()+"于"+ DateUtils.date2String(new Date())+"取消了该笔订单!";

        ShopOrder2C shopOrder2C = JSON.parseObject(consumerOrder.getFeature(),ShopOrder2C.class);

        /**
         * 回退积分
         */
        if(shopOrder2C.getUsePoint()>0){
            YdWeixinUserInfo weixinUserInfo = ydWeixinUserInfoMapper.selectByUnionid(consumerOrder.getUnionid());
            if(weixinUserInfo!=null) {
                shopInfoService.rebackConsumptionPoint(weixinUserInfo.getId(), UserSourceEnum.WEIXIN.getType(), consumerOrder.getShopid(), shopOrder2C.getUsePoint());
            }
        }
        consumerOrder.setTotalAmount(AmountUtils.add(consumerOrder.getTotalAmount(),shopOrder2C.getDeMoney()));
        shopOrder2C.setUsePoint(0);
        shopOrder2C.setDeMoney(new BigDecimal("0.00"));
        shopOrder2C.setStatus(UserOrderStatusEnum.CANCELBYSHOP.getStatus());
        consumerOrder.setStatus(shopOrder2C.getStatus());
        consumerOrder.setFeature(JSON.toJSONString(shopOrder2C));
        consumerOrder.setManagerOptionHistory(managerOptionInfo);
        if(ydShopConsumerOrderMapper.updateByPrimaryKeySelective(consumerOrder)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"修改订单失败!");
        }

        this.updateOrder2OpenSearch(consumerOrder.getOrderid());

        return true;
    }

    @Override
    public boolean offlineToPay(String openid, String shopid, String orderid) {
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdShopConsumerOrder consumerOrder = ydShopConsumerOrderMapper.selectByOrderidRowLock(orderid);
        if(consumerOrder==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"订单不存在!");
        }
        if(!StringUtil.equals(consumerOrder.getShopid(),shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        if((consumerOrder.getStatus().intValue()<0)||(consumerOrder.getIsPay().intValue()>=0)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"已取消或者已付款订单不能操作!");
        }

        /**
         * 查操作员信息
         */
        Object object = userinfoService.getCpUserInfo(openid,shopid);
        if(object==null){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),"操作员信息不存在！");
        }

        UserInfoVO cpUserInfo = (UserInfoVO)object;
        String managerOptionInfo = "操作员:"+cpUserInfo.getMobile()+"于"+ DateUtils.date2String(new Date())+"设置了该订单已现金收款!";

        ShopOrder2C shopOrder2C = JSON.parseObject(consumerOrder.getFeature(),ShopOrder2C.class);
        shopOrder2C.setIsPay(1);

        consumerOrder.setIsPay(shopOrder2C.getIsPay());
        if(UserOrderStatusEnum.nameOf(consumerOrder.getStatus())==UserOrderStatusEnum.WAITEPAYPRE){
            shopOrder2C.setStatus(UserOrderStatusEnum.PAYFINISH.getStatus());
            consumerOrder.setStatus(shopOrder2C.getStatus());
            consumerOrder.setManagerOptionHistory(managerOptionInfo);
            consumerOrder.setFeature(JSON.toJSONString(shopOrder2C));
            if(ydShopConsumerOrderMapper.updateByPrimaryKeySelective(consumerOrder)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"操作失败!");
            }
        }else if((UserOrderStatusEnum.nameOf(consumerOrder.getStatus())==UserOrderStatusEnum.WAITEPAYPOST)||
                (UserOrderStatusEnum.nameOf(consumerOrder.getStatus())==UserOrderStatusEnum.SENDOUT)||
                (UserOrderStatusEnum.nameOf(consumerOrder.getStatus())==UserOrderStatusEnum.CONFIRM)){
            shopOrder2C.setStatus(UserOrderStatusEnum.FINISH.getStatus());
            consumerOrder.setStatus(shopOrder2C.getStatus());
            consumerOrder.setManagerOptionHistory(managerOptionInfo);
            consumerOrder.setFeature(JSON.toJSONString(shopOrder2C));
            if(ydShopConsumerOrderMapper.updateByPrimaryKeySelective(consumerOrder)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"操作失败!");
            }
        }else {
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"当前状态不能进行此操作!");
        }
        this.updateOrder2OpenSearch(consumerOrder.getOrderid());

        return true;
    }

    @Override
    public boolean userOrderTaking(String openid, String shopid, String orderid) {

        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdShopConsumerOrder consumerOrder = ydShopConsumerOrderMapper.selectByOrderidRowLock(orderid);
        if(consumerOrder == null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"订单不存在!");
        }
        if(!StringUtil.equals(consumerOrder.getShopid(),shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        UserOrderStatusEnum orderStatusEnum = UserOrderStatusEnum.nameOf(consumerOrder.getStatus());
        if(orderStatusEnum!=UserOrderStatusEnum.PAYFINISH&&orderStatusEnum!=UserOrderStatusEnum.WAITE){
            this.updateOrder2OpenSearch(consumerOrder.getOrderid());
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"当前订单状态不能进行接单操作!");
        }

        ShopOrder2C shopOrder2C = JSON.parseObject(consumerOrder.getFeature(),ShopOrder2C.class);
        shopOrder2C.setStatus(UserOrderStatusEnum.READY.getStatus());
        consumerOrder.setStatus(shopOrder2C.getStatus());
        if(ydShopConsumerOrderMapper.updateByPrimaryKeySelective(consumerOrder)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"接单操作失败!");
        }

        this.updateOrder2OpenSearch(consumerOrder.getOrderid());

        return true;
    }

    @Override
    public boolean userOrderSendOut(String openid, String shopid, String orderid, String desc) {
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdShopConsumerOrder consumerOrder = ydShopConsumerOrderMapper.selectByOrderidRowLock(orderid);
        if(consumerOrder == null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"订单不存在!");
        }
        if(!StringUtil.equals(consumerOrder.getShopid(),shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        UserOrderStatusEnum orderStatusEnum = UserOrderStatusEnum.nameOf(consumerOrder.getStatus());
        if(orderStatusEnum!=UserOrderStatusEnum.PAYFINISH&&orderStatusEnum!=UserOrderStatusEnum.READY&&orderStatusEnum!=UserOrderStatusEnum.WAITE){
            this.updateOrder2OpenSearch(consumerOrder.getOrderid());
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"当前订单状态不能进行发货操作!");
        }

        ShopOrder2C shopOrder2C = JSON.parseObject(consumerOrder.getFeature(),ShopOrder2C.class);
        shopOrder2C.setStatus(UserOrderStatusEnum.SENDOUT.getStatus());
        consumerOrder.setStatus(shopOrder2C.getStatus());
        if(StringUtil.isNotEmpty(desc)){
            consumerOrder.setShopDesc(desc);
        }
        if(ydShopConsumerOrderMapper.updateByPrimaryKeySelective(consumerOrder)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"发货操作失败!");
        }

        this.updateOrder2OpenSearch(consumerOrder.getOrderid());

        return true;
    }

    @Override
    public List<SearchOrderDataResultVO> queryShopOrderIndoor(String openid, String shopid, Date queryDate, String tableid, String status, Integer pageIndex, Integer count) {
        if(!(userinfoService.checkIsManager(openid,shopid)||
                userinfoService.checkIsWaiter(openid,shopid)||
                userinfoService.checkIsOwner(openid,shopid))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        List<SearchOrderDataResultVO> resultVOS = null;

        List<Integer> statusList = null;
        if(StringUtil.isNotEmpty(status)){
            String[] strings = status.split(",");
            statusList = new ArrayList<>();
            for(int i=0; i<strings.length; i++){
                statusList.add(new Integer(strings[i].trim()));
            }
        }

        if(StringUtil.isEmpty(tableid)){
            resultVOS = orderSearchService.queryShopAllOrderIndoor(shopid,queryDate,statusList,pageIndex,count);
        }else {
            resultVOS = orderSearchService.queryShopOrderIndoorByTableId(shopid,tableid,queryDate,statusList,pageIndex,count);
        }

        return resultVOS;
    }

    @Override
    public List<SearchOrderDataResultVO> queryShopAllMoneyOrder(String openid, String shopid, Date queryDate, String status, Integer pageIndex, Integer count) {
        if(!(userinfoService.checkIsManager(openid,shopid)||
                userinfoService.checkIsWaiter(openid,shopid)||
                userinfoService.checkIsOwner(openid,shopid))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        List<SearchOrderDataResultVO> resultVOS = null;

        List<Integer> statusList = null;
        if(StringUtil.isNotEmpty(status)){
            String[] strings = status.split(",");
            statusList = new ArrayList<>();
            for(int i=0; i<strings.length; i++){
                statusList.add(new Integer(strings[i].trim()));
            }
        }

        resultVOS = orderSearchService.queryShopAllMoneyOrder(shopid,queryDate,statusList,pageIndex,count);


        return resultVOS;
    }

    @Override
    public List<SearchOrderDataResultVO> queryShopOrderOnline(String openid, String shopid, Date queryDate, String channelid, String status, Integer pageIndex, Integer count) {
        if(!(userinfoService.checkIsManager(openid,shopid)||
                userinfoService.checkIsWaiter(openid,shopid)||
                userinfoService.checkIsOwner(openid,shopid))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        List<SearchOrderDataResultVO> resultVOS = null;

        List<Integer> statusList = null;
        if(StringUtil.isNotEmpty(status)){
            String[] strings = status.split(",");
            statusList = new ArrayList<>();
            for(int i=0; i<strings.length; i++){
                statusList.add(new Integer(strings[i].trim()));
            }
        }

        if(StringUtil.isEmpty(channelid)){
            resultVOS = orderSearchService.queryShopOnlineOrder(shopid,queryDate,statusList,pageIndex,count);
        }else {
            resultVOS = orderSearchService.queryShopOnlineOrderByChannelId(shopid,channelid,queryDate,statusList,pageIndex,count);
        }

        return resultVOS;
    }

    @Override
    public List<SearchOrderDataResultVO> queryShopAllOrder(String openid, String shopid, Date queryDate, String entercode, String status, Integer pageIndex, Integer count) {
        if(!(userinfoService.checkIsManager(openid,shopid)||
                userinfoService.checkIsWaiter(openid,shopid)||
                userinfoService.checkIsOwner(openid,shopid))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        List<SearchOrderDataResultVO> resultVOS = null;

        List<Integer> statusList = null;
        if(StringUtil.isNotEmpty(status)){
            String[] strings = status.split(",");
            statusList = new ArrayList<>();
            for(int i=0; i<strings.length; i++){
                statusList.add(new Integer(strings[i].trim()));
            }
        }


        resultVOS = orderSearchService.queryShopAllOrder(shopid,entercode,queryDate,statusList,pageIndex,count);


        return resultVOS;
    }

    @Override
    public ShopOrderExt2CP queryUserOrderDetail(String openid, String shopid, String orderid) {
        if(!(userinfoService.checkIsManager(openid,shopid)||
                userinfoService.checkIsWaiter(openid,shopid)||
                userinfoService.checkIsOwner(openid,shopid))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdShopConsumerOrder consumerOrder = ydShopConsumerOrderMapper.selectByOrderid(orderid);
        if(consumerOrder==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "订单不存在!");
        }

        if(!StringUtil.equals(consumerOrder.getShopid(),shopid)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "不是此店铺的订单!");
        }

        ShopOrder2C shopOrder2C = JSON.parseObject(consumerOrder.getFeature(),ShopOrder2C.class);
        shopOrder2C.setOrderDate(consumerOrder.getCreateDate());
        shopOrder2C.setStatus(consumerOrder.getStatus());
        shopOrder2C.setCpDesc(consumerOrder.getShopDesc());

        /**判断是不是堂食**/
        shopOrder2C.setIsIndoor(0);
        if(StringUtil.isNotEmpty(shopOrder2C.getEnterCode())) {
            TypeEnum typeEnum = TypeEnum.getTypeOfSN(shopOrder2C.getEnterCode());
            if(typeEnum==TypeEnum.DININGTABLE){
                shopOrder2C.setIsIndoor(1);
            }
        }

        UserOrderDeliveryInfoVO deliveryInfoVO = null;
        UserAddressInfoVO userAddressInfoVO = null;
        Map<String,Object> deliveryMap = null;
        if(StringUtil.isNotEmpty(consumerOrder.getDelivery())){
            deliveryMap = JSON.parseObject(consumerOrder.getDelivery(),Map.class);
            if(deliveryMap.containsKey(Constant.DELIVERYINFO)){
                deliveryInfoVO = ((JSONObject)deliveryMap.get(Constant.DELIVERYINFO)).toJavaObject(UserOrderDeliveryInfoVO.class);
            }
            if(deliveryMap.containsKey(Constant.USERADDRESSINFO)){
                userAddressInfoVO = ((JSONObject)deliveryMap.get(Constant.USERADDRESSINFO)).toJavaObject(UserAddressInfoVO.class);
            }

        }
        ShopOrderExt2CP shopOrderExt2C = new ShopOrderExt2CP();
        shopOrderExt2C.setShopOrder2C(shopOrder2C);
        shopOrderExt2C.setDeliveryInfoVO(deliveryInfoVO);
        shopOrderExt2C.setUserAddressInfoVO(userAddressInfoVO);
        shopOrderExt2C.setCpOptionHistory(consumerOrder.getManagerOptionHistoryList());
        if(StringUtil.isNotEmpty(consumerOrder.getPayqrcode())){
            Map<String,Object> bizPayUrl = JSON.parseObject(consumerOrder.getPayqrcode(),Map.class);
            shopOrderExt2C.setBizPay(bizPayUrl);
        }
        shopOrderExt2C.setActualAmount(consumerOrder.getTotalAmount());

        return shopOrderExt2C;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String createMoneyOrder(String openid, String shopid, BigDecimal money, String name, String desc) {

        if(!(userinfoService.checkIsManager(openid,shopid)||
                userinfoService.checkIsWaiter(openid,shopid)||
                userinfoService.checkIsOwner(openid,shopid))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        UserInfoVO userInfoVO = (UserInfoVO)userinfoService.getCpUserInfo(openid,shopid);
        if(StringUtil.isEmpty(name)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "名称不能为空!");
        }
        if(money==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "金额不能为空!");
        }
        if(AmountUtils.changeY2F(money)<=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "金额不正确!");
        }

        List<YdWeixinServiceConfig> serviceConfigs = ydWeixinServiceConfigMapper.selectByShopId(shopid);
        if(serviceConfigs==null||serviceConfigs.size()<=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "支付商户号还没有开通!");
        }
        YdWeixinServiceConfig ydWeixinServiceConfig = null;
        for(YdWeixinServiceConfig serviceConfig: serviceConfigs){
            if(StringUtil.isNotEmpty(serviceConfig.getWeixinpaySubMchId())){
                ydWeixinServiceConfig = serviceConfig;
                break;
            }
        }
        if(ydWeixinServiceConfig == null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "支付商户号还没有开通!");
        }

        ShopInfoDTO shopInfoDTO = shopInfoService.getShopInfo(shopid);
        if(shopInfoDTO==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "店铺不存在!");
        }

        ShopOrder2C shopOrder2C = new ShopOrder2C();
        shopOrder2C.setIsIndoor(2);//收钱宝一定是当面收，所以必须是店内订单，但与堂食分开进行区别，所以设置为2
        if(StringUtil.isNotEmpty(desc)){
            shopOrder2C.setCpDesc(desc.trim());
        }
        shopOrder2C.setShopid(shopid);
        shopOrder2C.setOrderName(name.trim());
        shopOrder2C.setIsPay(-1);
        shopOrder2C.setEnterCode(shopid);
        shopOrder2C.setEnterName(userInfoVO.getMobile()+":"+userInfoVO.getNick());
        shopOrder2C.setTotalAmount(AmountUtils.bigDecimalBy2(money));
        shopOrder2C.setDisTotalAmount(shopOrder2C.getTotalAmount());
        shopOrder2C.setDiscount(1.0);
        shopOrder2C.setPayMode(0);
        shopOrder2C.setStatus(UserOrderStatusEnum.WAITE.getStatus());
        shopOrder2C.setOrderDate(new Date());
        shopOrder2C.setShoppingOrderSkuVOList(new ArrayList<>());
        shopOrder2C.setOrderid(RandomUtil.getSNCode(TypeEnum.MONEYORDER));

        /**
         * 准备保存订单
         */
        YdShopConsumerOrder ydShopConsumerOrder = new YdShopConsumerOrder();
        ydShopConsumerOrder.setChannelId(shopOrder2C.getEnterCode());
        ydShopConsumerOrder.setIsPay(-1);
        ydShopConsumerOrder.setUnionid(userInfoVO.getMobile());
        ydShopConsumerOrder.setOpenid(userInfoVO.getMobile());
        ydShopConsumerOrder.setWeixinConfigId(ydWeixinServiceConfig.getWeixinConfigId());
        ydShopConsumerOrder.setPayMode(shopOrder2C.getPayMode());
        ydShopConsumerOrder.setShopid(shopOrder2C.getShopid());
        ydShopConsumerOrder.setStatus(shopOrder2C.getStatus());
        ydShopConsumerOrder.setOrderid(shopOrder2C.getOrderid());
        ydShopConsumerOrder.setOrderName(shopOrder2C.getOrderName());
        ydShopConsumerOrder.setOrderType(shopOrder2C.getOrderType());
        ydShopConsumerOrder.setTotalAmount(shopOrder2C.getDisTotalAmount());

        /**
         * 保险起见，加一层分布式事务缓存锁,超时时间为1秒
         */
        boolean lockIsTrue = redisManager.lockWithTimeout(shopOrder2C.getShopid()+"neworder",shopOrder2C.getOrderid(),1500,1000);
        boolean isFirstOrderToday = false;
        YdShoporderStatistics shoporderStatistics = ydShoporderStatisticsMapper.selectByShopidRowLock(shopOrder2C.getShopid(), new Date());
        if (shoporderStatistics == null) {
            isFirstOrderToday = true;
            shoporderStatistics = new YdShoporderStatistics();
            shoporderStatistics.setShopid(shopOrder2C.getShopid());
            shoporderStatistics.setOrderCount(0);
            shoporderStatistics.setOrderDate(new Date());
            String bagStr = shopInfoDTO.getFeature(Constant.BAGORDERNUM);
            if(StringUtil.isBlank(bagStr)){
                shoporderStatistics.setBagCount(0);
            }else {
                shoporderStatistics.setBagCount(Integer.valueOf(bagStr));
            }
            shoporderStatistics.setReceiveAmount(new BigDecimal("0.00"));
        }

        shoporderStatistics.setOrderCount(shoporderStatistics.getOrderCount()+1);

        /**
         * 创建订单，写订单表
         */
        shopOrder2C.setPrintCount(shoporderStatistics.getOrderCount());
        ydShopConsumerOrder.setFeature(JSON.toJSONString(shopOrder2C));
        if(ydShopConsumerOrderMapper.insert(ydShopConsumerOrder)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "订单创建失败!");
        }

        /**
         * 写订单统计表
         */
        if(!isFirstOrderToday){
            shoporderStatistics.setLastid(ydShopConsumerOrder.getId());
            if(ydShoporderStatisticsMapper.updateByPrimaryKeySelective(shoporderStatistics)<=0){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "订单创建失败，请稍后再试！");
            }

        }else {
            shoporderStatistics.setFirstid(ydShopConsumerOrder.getId());
            shoporderStatistics.setLastid(ydShopConsumerOrder.getId());
            shoporderStatistics.setWithdrawals(0);
            if(ydShoporderStatisticsMapper.insert(shoporderStatistics)<=0){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "订单创建失败，请稍后再试！");
            }

        }

        if(lockIsTrue){
            /**
             * 释放锁
             */
            redisManager.releaseLock(shopOrder2C.getShopid()+"neworder",shopOrder2C.getOrderid());
        }

        /**
         * 发订单超时取消消息（半小时关闭）
         */
        Map<String,Object> timeoutMap = new HashMap<>();
        timeoutMap.put(Constant.MQTAG,MqTagEnum.USERORDERTIMEOUT.getTag());
        timeoutMap.put(Constant.MQBODY,ydShopConsumerOrder.getOrderid());
        mqMessageService.sendMessage(ydShopConsumerOrder.getOrderid()+"timeout",MqTagEnum.USERORDERTIMEOUT,JSON.toJSONString(timeoutMap),1800000);

        /**
         * 发消息同步订单信息到opensearch
         */
        Map<String,Object> msgMap = new HashMap<>();
        msgMap.put(Constant.MQTAG,MqTagEnum.USERORDERNEW.getTag());
        msgMap.put(Constant.MQBODY,ydShopConsumerOrder.getOrderid());
        String msgId = mqMessageService.sendMessage(ydShopConsumerOrder.getOrderid()+"new",ydShopConsumerOrder.getOrderid(),MqTagEnum.USERORDERNEW,JSON.toJSONString(msgMap));

        return shopOrder2C.getOrderid();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> microPay(String openid, String orderid, String authCode) throws Exception {
        YdShopConsumerOrder consumerOrder = ydShopConsumerOrderMapper.selectByOrderidRowLock(orderid);
        if(consumerOrder==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "订单不存在!");
        }
        if(!(userinfoService.checkIsManager(openid,consumerOrder.getShopid())||
                userinfoService.checkIsWaiter(openid,consumerOrder.getShopid())||
                userinfoService.checkIsOwner(openid,consumerOrder.getShopid()))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        YdWeixinServiceConfig serviceConfig = ydWeixinServiceConfigMapper.selectByWeixinConfigId(consumerOrder.getWeixinConfigId());
        if(serviceConfig==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "订单对应的APP授权信息不存在!");
        }

        PayTypeEnum payTypeEnum = PayTypeEnum.WEIXINS;
        if(StringUtil.contains(
                DiamondWeiXinInfoConfigHolder.getInstance().getPublicMchIds(),
                serviceConfig.getWeixinpaySubMchId())){
            payTypeEnum = PayTypeEnum.WEIXIN2B;
        }

        WeiXinPayRequestDO weiXinPayRequestDO = new WeiXinPayRequestDO();
        if(payTypeEnum==PayTypeEnum.WEIXINS) {
            weiXinPayRequestDO.setSub_appid(serviceConfig.getAppid());
            weiXinPayRequestDO.setSub_mch_id(serviceConfig.getWeixinpaySubMchId());
        }else if(payTypeEnum==PayTypeEnum.WEIXIN2B) {
            weiXinPayRequestDO.setAppid(serviceConfig.getAppid());
            weiXinPayRequestDO.setMch_id(serviceConfig.getWeixinpaySubMchId());
        }
        weiXinPayRequestDO.setBody("引灯智能店铺-"+consumerOrder.getOrderName());
        weiXinPayRequestDO.setOut_trade_no(orderid);
        weiXinPayRequestDO.setTotal_fee(AmountUtils.changeY2F(consumerOrder.getTotalAmount()));
        weiXinPayRequestDO.setAuth_code(authCode);
        InetAddress addr = InetAddress.getLocalHost();
        String ip=addr.getHostAddress().toString(); //获取本机ip
        weiXinPayRequestDO.setSpbill_create_ip(ip);
        weiXinPayRequestDO.setTrade_type(null);

        Map<String,Object> result = weiXinPayService.microPay(weiXinPayRequestDO);
        /**
         * 成功，更新订单表，
         */
        Boolean isSuccess = (Boolean)result.get("success");
        if(isSuccess){
            Map<String,Object> resultPayInfo = new HashMap<>();
            resultPayInfo.put(Constant.RESULTPAYINFO,result);
            String openid2c = (String)result.get("openid");
            ShopOrder2C shopOrder2C = JSON.parseObject(consumerOrder.getFeature(),ShopOrder2C.class);
            shopOrder2C.setIsPay(0);
            shopOrder2C.setStatus(UserOrderStatusEnum.FINISH.getStatus());
            consumerOrder.setIsPay(shopOrder2C.getIsPay());
            consumerOrder.setStatus(shopOrder2C.getStatus());
            consumerOrder.setOpenid(openid2c);
            consumerOrder.setUnionid(openid2c+serviceConfig.getWeixinConfigId());
            consumerOrder.setFeature(JSON.toJSONString(shopOrder2C));
            consumerOrder.setResultPayInfo(JSON.toJSONString(resultPayInfo));
            if(ydShopConsumerOrderMapper.updateByPrimaryKeySelective(consumerOrder)<=0){
                logger.error("microPay 支付成功 但是更新订单支付状态失败！");
                this.updateMoneyOrderMsg(orderid,0);
            }else {
                this.updateOrder2OpenSearch(orderid);
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> queryWeiXinPayOrder(String openid, String orderid) throws Exception {
        YdShopConsumerOrder consumerOrder = ydShopConsumerOrderMapper.selectByOrderid(orderid);
        if(consumerOrder==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "订单不存在!");
        }
        if(!(userinfoService.checkIsManager(openid,consumerOrder.getShopid())||
                userinfoService.checkIsWaiter(openid,consumerOrder.getShopid())||
                userinfoService.checkIsOwner(openid,consumerOrder.getShopid()))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        YdWeixinServiceConfig serviceConfig = ydWeixinServiceConfigMapper.selectByWeixinConfigId(consumerOrder.getWeixinConfigId());
        if(serviceConfig==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "商户不存在!");
        }

        Map<String,Object> result = weiXinPayService.queryOrderByWeiXin(serviceConfig.getAppid(),serviceConfig.getWeixinpaySubMchId(),orderid);

        return result;
    }

    protected String updateOrder2OpenSearch(String orderid){
        /**
         * 发消息同步订单信息到opensearch
         */
        Map<String,Object> msgMap = new HashMap<>();
        msgMap.put(Constant.MQTAG, MqTagEnum.USERORDERUPDATE.getTag());
        msgMap.put(Constant.MQBODY,orderid);
        String msgId = mqMessageService.sendMessage(orderid+"updatecp",orderid,MqTagEnum.USERORDERUPDATE,JSON.toJSONString(msgMap));
        return msgId;
    }

    protected String updateMoneyOrderMsg(String orderid,Integer count){
        /**
         * 发消息同步订单信息到opensearch
         */
        if(count>3){
            return null;
        }
        Map<String,Object> msgMap = new HashMap<>();
        Map<String,Object> payMap = new HashMap<>();
        msgMap.put(Constant.MQTAG, MqTagEnum.MicroPay.getTag());
        payMap.put(Constant.ORDERID,orderid);
        payMap.put("count",count);
        msgMap.put(Constant.MQBODY,payMap);
        String msgId = mqMessageService.sendMessage(orderid+"MicroPay",MqTagEnum.MicroPay,JSON.toJSONString(msgMap),12000);
        return msgId;
    }


}
