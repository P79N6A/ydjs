package com.yd.ydsp.biz.cp.impl;

import com.alibaba.fastjson.JSON;
import com.yd.ydsp.biz.config.DiamondYdPayPointConfigHolder;
import com.yd.ydsp.biz.config.DiamondYdSystemConfigHolder;
import com.yd.ydsp.biz.cp.ShopInfoService;
import com.yd.ydsp.biz.cp.model.ApplyShopInfoVO;
import com.yd.ydsp.biz.message.MqMessageService;
import com.yd.ydsp.biz.pay.YdPayService;
import com.yd.ydsp.biz.pay.model.YdPayResponse;
import com.yd.ydsp.biz.pay.model.YeePayRequestDO;
import com.yd.ydsp.biz.user.UserinfoService;
import com.yd.ydsp.biz.user.model.CheckMobileCodeTypeEnum;
import com.yd.ydsp.client.domian.ShopInfoDTO;
import com.yd.ydsp.client.domian.ShopInfoVO;
import com.yd.ydsp.client.domian.openshop.YdCpMemberCardVO;
import com.yd.ydsp.client.domian.openshop.YdShopHoursInfoVO;
import com.yd.ydsp.client.domian.paypoint.CPOrderDTO;
import com.yd.ydsp.client.domian.paypoint.CpUserInfoDTO;
import com.yd.ydsp.client.domian.paypoint.ShopSimpleInfoVO;
import com.yd.ydsp.common.Exception.ExceptionConstant;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.constants.paypoint.ShopSupportFlagConstants;
import com.yd.ydsp.common.enums.DeliveryTypeEnum;
import com.yd.ydsp.common.enums.OrderStatusEnum;
import com.yd.ydsp.common.enums.PayOrderTypeEnum;
import com.yd.ydsp.common.enums.paypoint.MemberCardTypeEnum;
import com.yd.ydsp.common.enums.paypoint.ShopStatusEnum;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 *
 * @author zengyixun
 * @date 17/9/12
 */
public class ShopInfoServiceImpl implements ShopInfoService {

    public static final Logger logger = LoggerFactory.getLogger(ShopInfoServiceImpl.class);

    @Resource
    private DozerBeanMapper doMapper;

    @Resource
    private YdPaypointShopInfoMapper ydPaypointShopInfoMapper;
    @Resource
    private YdPaypointShopInfoExtMapper ydPaypointShopInfoExtMapper;
    @Resource
    private YdPaypointShopworkerMapper ydPaypointShopworkerMapper;

    @Resource
    private YdPaypointCpuserInfoMapper ydPaypointCpuserInfoMapper;

    @Resource
    private YdPaypointCpOrderMapper ydPaypointCpOrderMapper;

    @Resource
    private YdPaypointUserContractMapper ydPaypointUserContractMapper;
    @Resource
    private YdPaypointContractinfoMapper ydPaypointContractinfoMapper;
    @Resource
    private YdCpMemberCardMapper ydCpMemberCardMapper;
    @Resource
    private YdShopUserDataMapper ydShopUserDataMapper;
    @Resource
    private YdPaypointCpdeviceInfoMapper ydPaypointCpdeviceInfoMapper;

    @Resource
    private UserinfoService userinfoService;

    @Resource
    private YdPayService ydPayService;
    @Resource
    private MqMessageService mqMessageService;
    @Resource
    private RedisManager redisManager;

    @Override
    public List<String> getShopsByOwner(String openid) {
        if(StringUtil.isBlank(openid)){
            return null;
        }
        YdPaypointCpuserInfo cpuserInfoDO = ydPaypointCpuserInfoMapper.selectByOpenid(openid.trim());
        if(cpuserInfoDO==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "用户不存在！");
        }
        CpUserInfoDTO cpUserInfoDTO = doMapper.map(cpuserInfoDO,CpUserInfoDTO.class);
        return cpUserInfoDTO.getListFeature(Constant.CP_SHOPS_OWNER);
    }

    @Override
    public List<String> getShopsByManager(String openid) {
        if(StringUtil.isBlank(openid)){
            return null;
        }
        YdPaypointCpuserInfo cpuserInfoDO = ydPaypointCpuserInfoMapper.selectByOpenid(openid.trim());
        if(cpuserInfoDO==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "用户不存在！");
        }
        CpUserInfoDTO cpUserInfoDTO = doMapper.map(cpuserInfoDO,CpUserInfoDTO.class);
        return cpUserInfoDTO.getListFeature(Constant.CP_SHOPS_MANAGER);
    }

    @Override
    public ShopSimpleInfoVO getDefaultShop(String openid) {
        if(StringUtil.isBlank(openid)){
            return null;
        }
        YdPaypointCpuserInfo paypointCpuserInfo = ydPaypointCpuserInfoMapper.selectByOpenid(openid.trim());
        if(paypointCpuserInfo==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "用户不存在！");
        }
        CpUserInfoDTO cpUserInfoDTO = doMapper.map(paypointCpuserInfo,CpUserInfoDTO.class);
        if(cpUserInfoDTO==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "内部错误！");
        }
        String shopid = cpUserInfoDTO.getFeature(Constant.DEFAULT_SHOP);
        if(StringUtil.isBlank(shopid)){
            return null;
        }

        YdPaypointShopInfo ydPaypointShopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
        if(ydPaypointShopInfo!=null) {
            if (ydPaypointShopInfo.getStatus() > 1) {
                ShopSimpleInfoVO shopSimpleInfoVO = new ShopSimpleInfoVO();
                shopSimpleInfoVO.setShopid(shopid);
                shopSimpleInfoVO.setName(ydPaypointShopInfo.getName());
                return shopSimpleInfoVO;
            }
        }
        return null;

    }

    @Override
    public List<ShopSimpleInfoVO> getShopsByCpUser(String openid) {
        if(StringUtil.isBlank(openid)){
            return new ArrayList<>();
        }
        YdPaypointCpuserInfo cpuserInfoDO = ydPaypointCpuserInfoMapper.selectByOpenid(openid.trim());
        if(cpuserInfoDO==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "用户不存在！");
        }
        CpUserInfoDTO cpUserInfoDTO = doMapper.map(cpuserInfoDO,CpUserInfoDTO.class);
        List<String> shops = cpUserInfoDTO.getListFeature(Constant.CP_SHOPS_MANAGER);
        List<String> shopsWaiter = cpUserInfoDTO.getListFeature(Constant.CP_SHOPS_WAITER);
        if(shops==null){
            shops = new ArrayList<>();
        }
        if(shopsWaiter==null){
            shopsWaiter = new ArrayList<>();
        }
        shops.removeAll(shopsWaiter);
        shops.addAll(shopsWaiter);
        List<ShopSimpleInfoVO> shopSimpleInfoVOS = new ArrayList<>();
        for(String shopid:shops){
            YdPaypointShopInfo ydPaypointShopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
            if(ydPaypointShopInfo!=null){
                if(ydPaypointShopInfo.getStatus()>1) {
                    ShopSimpleInfoVO shopSimpleInfoVO = new ShopSimpleInfoVO();
                    shopSimpleInfoVO.setShopid(shopid);
                    shopSimpleInfoVO.setName(ydPaypointShopInfo.getName());
                    shopSimpleInfoVOS.add(shopSimpleInfoVO);
                }
            }
        }
        return shopSimpleInfoVOS;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String applyShopJoin(String openid,String contractid,ApplyShopInfoVO shopInfoVO) {

        if(shopInfoVO==null){
            throw new YdException(ErrorCodeConstants.YD00905.getErrorCode(), "数据不能为空！");
        }
        if(StringUtil.isBlank(contractid)){
            contractid = "298820180130001";
        }

        String shopid;

        /**
         * 校验验证码
         */

        if (!userinfoService.checkCPUserMobileCode(openid, shopInfoVO.getMobile(), shopInfoVO.getMobileCheckCode(), null, CheckMobileCodeTypeEnum.BINDSHOPMOBILE)) {
            throw new YdException(ErrorCodeConstants.YD10006.getErrorCode(), ErrorCodeConstants.YD10006.getErrorMessage());
        }

        if(StringUtil.isBlank(shopInfoVO.getIdentityNumber())){
            throw new YdException(ExceptionConstant.IDENTITY_NUMBER_IS_NULL,ExceptionConstant.IDENTITY_NUMBER_IS_NULL);
        }
        ShopInfoDTO shopInfoDTO = doMapper.map(shopInfoVO, ShopInfoDTO.class);
        shopInfoDTO.addFeature(Constant.IDENTITY_NUMBER,shopInfoVO.getIdentityNumber());
        shopInfoDTO.setCorporation(shopInfoDTO.getContact());
        YdPaypointShopInfo shopInfoDO = doMapper.map(shopInfoDTO,YdPaypointShopInfo.class);
        if(shopInfoDO.getTrade()==null)
        {
            shopInfoDO.setTrade(0);
        }
        shopInfoDO.setStatus(ShopStatusEnum.APPLY.getType());

        /**
         * 新增还是修改逻辑
         */
        YdPaypointCpuserInfo cpuserInfoDO = ydPaypointCpuserInfoMapper.selectByOpenidLockRow(openid);
        if(cpuserInfoDO==null){
            throw new YdException(ExceptionConstant.IS_NOT_CP,ExceptionConstant.IS_NOT_CP);
        }
        YdPaypointCpuserInfo cpuserInfoByMobile = ydPaypointCpuserInfoMapper.selectByMobile(shopInfoVO.getMobile());
        if(cpuserInfoByMobile!=null){
            if(!StringUtil.equals(cpuserInfoByMobile.getOpenid(),cpuserInfoDO.getOpenid())){
                throw new YdException(ExceptionConstant.IS_NOT_CP,"该手机号已经被其它微信用户使用！");
            }
        }
        YdPaypointShopInfo shopInfoWithName = ydPaypointShopInfoMapper.selectByShopName(shopInfoDO.getName());

        if(StringUtil.isEmpty(shopInfoDO.getShopid())) {
            //新增
            if(shopInfoWithName!=null){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"店铺名称已经存在！");
            }
            shopInfoDO.setShopid(RandomUtil.getSNCode(TypeEnum.SHOP));
            ydPaypointShopInfoMapper.insert(shopInfoDO);
            //在合作伙伴用户记录中增加他所属于的店铺及角色
            CpUserInfoDTO cpUserInfoDTO = doMapper.map(cpuserInfoDO,CpUserInfoDTO.class);
            cpUserInfoDTO.addListFeature(Constant.CP_SHOPS_OWNER,shopInfoDO.getShopid());
            cpUserInfoDTO.addListFeature(Constant.CP_SHOPS_MANAGER,shopInfoDO.getShopid());
            /**
             * 这里不要设置了，应该在支付成功后，再将其设置为默认店
             */
//            cpUserInfoDTO.addFeature(Constant.DEFAULT_SHOP,shopInfoDO.getShopid());
            cpuserInfoDO.setFeature(cpUserInfoDTO.getFeature());
            if(StringUtil.isBlank(cpuserInfoDO.getMobile())) {
                cpuserInfoDO.setMobile(shopInfoVO.getMobile());
            }else{
                /**
                 * 如果微信用户已经绑定公众号手机，则看是不是与此次申请负责人手机一致，不一致则不能增加
                 */
                if(!StringUtil.equals(cpuserInfoDO.getMobile(),shopInfoVO.getMobile())){
                    throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"负责人手机与公众号绑定手机号不一致！");
                }
            }
            ydPaypointCpuserInfoMapper.updateByPrimaryKey(cpuserInfoDO);
            shopid = shopInfoDO.getShopid();
            /**
             * 员工角色管理表新增
             */
            YdPaypointShopworker shopworker = new YdPaypointShopworker();
            shopworker.setShopid(shopid);
            shopworker.setOwnerMobile(shopInfoDO.getMobile());
            if(ydPaypointShopworkerMapper.insert(shopworker)<=0){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "系统异常，请稍后再试！");
            }

        }else {
            //修改
            if(!userinfoService.checkIsOwner(openid,shopInfoDO.getShopid())){
                /**
                 * 如果不是这个店铺的owner，不能修改
                 */
                throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
            }
            if(shopInfoWithName!=null){
                if(!StringUtil.equals(shopInfoWithName.getShopid(),shopInfoDO.getShopid())) {
                    throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "店铺名称已经存在！");
                }
            }
            YdPaypointShopInfo shopInfoWitShopId = ydPaypointShopInfoMapper.selectByShopId(shopInfoDO.getShopid());
            if(shopInfoWitShopId==null){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "店铺不存在！");
            }
            if(shopInfoWitShopId.getStatus()>2||shopInfoWitShopId.getStatus()<0){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "店铺已经入驻，不能修改！");
            }
            CpUserInfoDTO cpUserInfoDTO = doMapper.map(cpuserInfoDO,CpUserInfoDTO.class);
            cpuserInfoDO.setFeature(cpUserInfoDTO.getFeature());
            if(StringUtil.isBlank(cpuserInfoDO.getMobile())) {
                cpuserInfoDO.setMobile(shopInfoVO.getMobile());
            }else{
                /**
                 * 如果微信用户已经绑定公众号手机，则看是不是与此次申请负责人手机一致，不一致则不能增加
                 */
                if(!StringUtil.equals(cpuserInfoDO.getMobile(),shopInfoVO.getMobile())){
                    throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"负责人手机与公众号绑定手机号不一致！");
                }
            }
            YdPaypointShopInfo ydPaypointShopInfo = ydPaypointShopInfoMapper.selectByShopId(shopInfoDO.getShopid());
            shopInfoDO.setId(ydPaypointShopInfo.getId());
            ydPaypointShopInfoMapper.updateByPrimaryKeySelective(shopInfoDO);
            ydPaypointCpuserInfoMapper.updateByPrimaryKey(cpuserInfoDO);
            shopid = shopInfoDO.getShopid();
            /**
             * 员工角色管理表更新
             */
            YdPaypointShopworker shopworker = ydPaypointShopworkerMapper.selectByShopid(shopid);
            shopworker.setOwnerMobile(shopInfoDO.getMobile());
            ydPaypointShopworkerMapper.updateByPrimaryKeySelective(shopworker);
        }

        /**
         * 开始签约流程
         */
        YdPaypointContractinfo contractinfo = ydPaypointContractinfoMapper.selectByContractId(contractid);
        if(contractinfo==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "合约信息不存在！");
        }
        YdPaypointUserContract userContract = ydPaypointUserContractMapper.selectByOpenidAndContractIdAndShopid(openid,contractid,shopInfoDO.getShopid());
        if(userContract==null){
            userContract = new YdPaypointUserContract();
            userContract.setContractId(contractid);
            userContract.setShopid(shopInfoDO.getShopid());
            userContract.setUserName(shopInfoDO.getCorporation());
            userContract.setIdentityNumber(shopInfoVO.getIdentityNumber());
            userContract.setMobile(shopInfoDO.getMobile());
            userContract.setOpenid(openid);
            userContract.setContractName(contractinfo.getContractName());
            userContract.setContractScene(contractinfo.getContractScene());
            userContract.setContractUrl(contractinfo.getContractUrl());
            if(ydPaypointUserContractMapper.insert(userContract)<=0){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "签约失败请检查数据！");
            }

        }else {

            userContract.setContractId(contractid);
            userContract.setShopid(shopInfoDO.getShopid());
            userContract.setUserName(shopInfoDO.getCorporation());
            userContract.setIdentityNumber(shopInfoVO.getIdentityNumber());
            userContract.setMobile(shopInfoDO.getMobile());
            userContract.setOpenid(openid);
            userContract.setContractName(contractinfo.getContractName());
            userContract.setContractScene(contractinfo.getContractScene());
            userContract.setContractUrl(contractinfo.getContractUrl());
            if(ydPaypointUserContractMapper.updateByPrimaryKeySelective(userContract)<=0){
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "签约失败请检查数据！");
            }
        }

        return shopid;
    }

    @Override
    public ApplyShopInfoVO getApplyShopInfoByShopId(String shopid) {
        if(StringUtil.isEmpty(shopid)) {
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "店铺id不能为空！");
        }
        YdPaypointShopInfo ydPaypointShopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
        if(ydPaypointShopInfo==null){
            return null;
        }
        ShopInfoDTO shopInfoDTO = doMapper.map(ydPaypointShopInfo,ShopInfoDTO.class);
        ApplyShopInfoVO result = doMapper.map(shopInfoDTO,ApplyShopInfoVO.class);
        result.setShopStatus(ydPaypointShopInfo.getStatus());
        result.setIdentityNumber(shopInfoDTO.getFeature(Constant.IDENTITY_NUMBER));
        return result;

    }

    @Override
    public ShopInfoDTO getShopInfo(String shopid) {
        if(StringUtil.isEmpty(shopid)) {
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "店铺id不能为空！");
        }
        YdPaypointShopInfo ydPaypointShopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
        if(ydPaypointShopInfo==null){
            return null;
        }
        ShopInfoDTO shopInfoDTO = doMapper.map(ydPaypointShopInfo,ShopInfoDTO.class);
        return shopInfoDTO;
    }

//    @Override
//    public boolean setShopInfoByOwner(String openid, ShopInfoVO shopInfoVO) {
//        if(!userinfoService.checkIsOwner(openid,shopInfoVO.getShopid())) {
//            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
//        }
//        YdPaypointShopInfo ydPaypointShopInfo = ydPaypointShopInfoMapper.selectByShopIdRowLock(shopInfoVO.getShopid());
//        if(ydPaypointShopInfo==null){
//            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "店铺不存在！");
//        }
//        ydPaypointShopInfo.setName(shopInfoVO.getName());
//        ydPaypointShopInfo.setShopImg(shopInfoVO.getShopImg());
//        ShopStatusEnum shopStatusEnum = ShopStatusEnum.nameOf(shopInfoVO.getStatus());
//        if(shopStatusEnum==ShopStatusEnum.NORMAL||shopStatusEnum==ShopStatusEnum.PAUSE){
//            ydPaypointShopInfo.setStatus(shopStatusEnum.getType());
//        }
//        ydPaypointShopInfo.setTelephone(shopInfoVO.getTelephone());
//        ydPaypointShopInfo.setDescription(shopInfoVO.getDescription());
//        ydPaypointShopInfo.setCountry(shopInfoVO.getCountry());
//        ydPaypointShopInfo.setProvince(shopInfoVO.getProvince());
//        ydPaypointShopInfo.setCity(shopInfoVO.getCity());
//        ydPaypointShopInfo.setDistrict(shopInfoVO.getDistrict());
//        ydPaypointShopInfo.setAddress(shopInfoVO.getAddress());
//        ydPaypointShopInfo.setZipcode(shopInfoVO.getZipcode());
//        if(ydPaypointShopInfoMapper.updateByPrimaryKeySelective(ydPaypointShopInfo)<=0){
//            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "店铺信息更新失败，请重试！");
//        }
//        return true;
//    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean setShopInfo(String openid, ShopInfoVO shopInfoVO) {
        if(!(userinfoService.checkIsOwner(openid,shopInfoVO.getShopid())||userinfoService.checkIsManager(openid,shopInfoVO.getShopid()))) {
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        DeliveryTypeEnum deliveryTypeEnum = DeliveryTypeEnum.nameOf(shopInfoVO.getDeliveryType());
        if(deliveryTypeEnum==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "物流配送方式设置不正确！");
        }

        YdPaypointShopInfo ydPaypointShopInfo = ydPaypointShopInfoMapper.selectByShopIdRowLock(shopInfoVO.getShopid());
        if(ydPaypointShopInfo==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "店铺不存在！");
        }

        ydPaypointShopInfo.setName(shopInfoVO.getName());
        ydPaypointShopInfo.setShopImg(shopInfoVO.getShopImg());
        ShopStatusEnum shopStatusEnum = ShopStatusEnum.nameOf(shopInfoVO.getStatus());
        if(shopStatusEnum==ShopStatusEnum.NORMAL||shopStatusEnum==ShopStatusEnum.PAUSE){
            ydPaypointShopInfo.setStatus(shopStatusEnum.getType());
        }
        ydPaypointShopInfo.setTelephone(shopInfoVO.getTelephone());
        ydPaypointShopInfo.setDescription(shopInfoVO.getDescription());
        ydPaypointShopInfo.setCountry(shopInfoVO.getCountry());
        ydPaypointShopInfo.setProvince(shopInfoVO.getProvince());
        ydPaypointShopInfo.setCity(shopInfoVO.getCity());
        ydPaypointShopInfo.setDistrict(shopInfoVO.getDistrict());
        ydPaypointShopInfo.setAddress(shopInfoVO.getAddress());
        ydPaypointShopInfo.setZipcode(shopInfoVO.getZipcode());

        ShopInfoDTO shopInfoDTO = doMapper.map(ydPaypointShopInfo,ShopInfoDTO.class);
        if(StringUtil.isNotEmpty(shopInfoVO.getPrinterNum())) {
            shopInfoDTO.addFeature(Constant.SHOP_PRINT, shopInfoVO.getPrinterNum());
        }
        if(shopInfoVO.getDeliveryType()==null){
            shopInfoVO.setDeliveryType(DeliveryTypeEnum.SHANGJIAPEI.getType());
        }
        /**
         * 设置物流配送方式
         */
        shopInfoDTO.addFeature(Constant.DELIVERYTYPE,shopInfoVO.getDeliveryType().toString());
        ydPaypointShopInfo.setFeature(shopInfoDTO.getFeature());
        if(ydPaypointShopInfoMapper.updateByPrimaryKeySelective(ydPaypointShopInfo)<=0){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "店铺信息更新失败，请重试！");
        }

        if(shopInfoVO.getMessage()!=null||shopInfoVO.getShopHoursInfoVO()!=null){
            YdPaypointShopInfoExt shopInfoExt = ydPaypointShopInfoExtMapper.selectByShopId(shopInfoVO.getShopid());
            if(shopInfoExt!=null){
                shopInfoExt.setMessage(shopInfoVO.getMessage());
                shopInfoExt.setShopHours(JSON.toJSONString(shopInfoVO.getShopHoursInfoVO()));

                if(ydPaypointShopInfoExtMapper.updateByPrimaryKeySelective(shopInfoExt)<=0){
                    throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "店铺信息更新失败，请重试！");
                }
            }
        }
        return true;
    }

    @Override
    public ShopInfoVO getShopInfoByInfoPage(String openid, String shopid) {

        if(StringUtil.isEmpty(shopid)) {
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "店铺id不能为空！");
        }
        if(!(userinfoService.checkIsOwner(openid,shopid)||userinfoService.checkIsManager(openid,shopid)||userinfoService.checkIsWaiter(openid,shopid))) {
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(), ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        YdPaypointShopInfo ydPaypointShopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
        if(ydPaypointShopInfo==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "店铺不存在！");
        }
        ShopInfoVO shopInfoVO = doMapper.map(ydPaypointShopInfo,ShopInfoVO.class);
        ShopInfoDTO shopInfoDTO = doMapper.map(ydPaypointShopInfo,ShopInfoDTO.class);
        if(StringUtil.isNotEmpty(shopInfoDTO.getFeature(Constant.SHOP_PRINT))){
            shopInfoVO.setPrinterNum(shopInfoDTO.getFeature(Constant.SHOP_PRINT));
            YdPaypointCpdeviceInfo cpdeviceInfo = ydPaypointCpdeviceInfoMapper.selectByDeviceId(shopInfoVO.getPrinterNum());
            shopInfoVO.setPrinterName(cpdeviceInfo.getName());
        }
        String dTye = shopInfoDTO.getFeature(Constant.DELIVERYTYPE);
        Integer deliveryType = DeliveryTypeEnum.SHANGJIAPEI.getType();
        if(StringUtil.isNotEmpty(dTye)){
            deliveryType = new Integer(dTye);
        }
        shopInfoVO.setDeliveryType(deliveryType);
        YdPaypointShopInfoExt paypointShopInfoExt = ydPaypointShopInfoExtMapper.selectByShopId(shopid);
        if(paypointShopInfoExt!=null){
//            logger.info("paypointShopInfoExt is:"+JSON.toJSONString(paypointShopInfoExt));
            shopInfoVO.setMessage(paypointShopInfoExt.getMessage());
            if(StringUtil.isNotEmpty(paypointShopInfoExt.getShopHours())) {
                YdShopHoursInfoVO shopHoursInfoVO = JSON.parseObject(paypointShopInfoExt.getShopHours(),YdShopHoursInfoVO.class);
                shopInfoVO.setShopHoursInfoVO(shopHoursInfoVO);
            }
        }
//        logger.info("result shopInfoVO:"+JSON.toJSONString(shopInfoVO));
        return shopInfoVO;
    }

    @Override
    public List<Map<String, Object>> getSetMealInfo() {
        List<Map<String, Object>> result = new ArrayList<>();
        result.add(JSON.parseObject(DiamondYdPayPointConfigHolder.getInstance().freeV,Map.class));
        result.add(JSON.parseObject(DiamondYdPayPointConfigHolder.getInstance().basicV,Map.class));
        result.add(JSON.parseObject(DiamondYdPayPointConfigHolder.getInstance().professionalV,Map.class));
        result.add(JSON.parseObject(DiamondYdPayPointConfigHolder.getInstance().goldV,Map.class));
        result.add(JSON.parseObject(DiamondYdPayPointConfigHolder.getInstance().diamondV,Map.class));
        return result;
    }

    @Override
    public Map<String, Integer> getTotalAmountBySetMeal(String setMealType, Integer monthNum,boolean isUp) {
        Map<String, Object> setMeal;
        Map<String, Integer> result = null;
        if(StringUtil.isEmpty(setMealType)){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),ErrorCodeConstants.PARAM_ERROR.getErrorMessage());
        }
        if(monthNum<=0){
            result.put(Constant.DISCOUNT, 0);
            result.put(Constant.ORIG,0);
            return result;
        }
        setMeal = getSetMeal(setMealType);

        if(!isUp) {
            if (monthNum < (Integer) setMeal.get("minMonth")) {
                monthNum = (Integer) setMeal.get("minMonth");
            }
        }

        result = new HashMap<>();
        Integer orig = AmountUtils.mul((Integer)setMeal.get("price"),monthNum).toBigInteger().intValue();

        result.put(Constant.ORIG,orig);
        if(orig<=0){
            return result;
        }
        if(monthNum>=12&&monthNum<24){
            Integer discount = AmountUtils.mul((BigDecimal)setMeal.get("y1"),orig).toBigInteger().intValue();
            if(orig>discount) {
                result.put(Constant.DISCOUNT, discount);
            }
        }
        if(monthNum>=24&&monthNum<36){
            Integer discount = AmountUtils.mul((BigDecimal) setMeal.get("y2"),orig).toBigInteger().intValue();
            if(orig>discount) {
                result.put(Constant.DISCOUNT, discount);
            }
        }
        if(monthNum>=36){
            Integer discount = AmountUtils.mul((BigDecimal)setMeal.get("y3"),orig).toBigInteger().intValue();
            if(orig>discount) {
                result.put(Constant.DISCOUNT, discount);
            }
        }

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String,Object> createOrderBySetMeal(String openid, String shopid, String setMealType, Integer monthNum,String ip) {
        if(!userinfoService.checkIsOwner(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),"只有负责人才能操作!");
        }
        Map<String,Object> result = new HashMap<>();
        Map<String, Integer> countMap = this.getTotalAmountBySetMeal(setMealType,monthNum,false);
        Integer totalCount = 0;
        if(countMap.containsKey(Constant.DISCOUNT)){
            totalCount = countMap.get(Constant.DISCOUNT);
        }else{
            totalCount = countMap.get(Constant.ORIG);
        }

        String orderId = RandomUtil.getSNCode(TypeEnum.CPORDER);
        CPOrderDTO orderDTO = new CPOrderDTO();
        orderDTO.setOrderid(orderId);
        orderDTO.setShopid(shopid);
        /**
         * 数据库里库的单位都是元
         */
        orderDTO.setTotalAmount(AmountUtils.changeF2YWithBigDecimal(totalCount));
        if(totalCount>0){
            orderDTO.setStatus(OrderStatusEnum.NEW.getStatus());
        }else{
            orderDTO.setStatus(OrderStatusEnum.CONFIRM.getStatus());
        }
        String beginDateStr = DateUtils.getToday();
        String endDateStr = DateUtils.plusMonth(beginDateStr,monthNum);
        orderDTO.addFeature(Constant.SETMEAL_BEGINDATE,beginDateStr);
        orderDTO.addFeature(Constant.SETMEAL_ENDDATE,endDateStr);
        orderDTO.addFeature(Constant.CP_ORDER_TYPE,setMealType);
        YdPaypointShopInfo shopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
        ShopInfoDTO shopInfoDTO = doMapper.map(shopInfo,ShopInfoDTO.class);
        if(shopInfo==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"没有查找到店铺!");
        }
        if(shopInfo.getStatus()>2){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"店铺已经购买了套餐，正常营业中！");
        }
        shopInfoDTO.addFeature(Constant.SHOP_SETMEAL_TYPE,setMealType);
        shopInfoDTO.setContractTimeBegin(DateUtils.dateStrToUdate(beginDateStr));
        shopInfoDTO.setContractTimeEnd(DateUtils.dateStrToUdate(endDateStr));
        if(totalCount>0){
            shopInfoDTO.setStatus(ShopStatusEnum.SETMEAL.getType());
        }else{
            shopInfoDTO.setStatus(ShopStatusEnum.NORMAL.getType());
        }

        /**
         * 开始插订单表以及修改shopInfo表的数据
         */

        YdPaypointCpOrder cpOrder = doMapper.map(orderDTO,YdPaypointCpOrder.class);
        cpOrder.setOpenid(openid);
        cpOrder.setOrderType(0);
        if(ydPaypointCpOrderMapper.insert(cpOrder)<=0){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"创建订单失败!");
        }

        shopInfo = doMapper.map(shopInfoDTO,YdPaypointShopInfo.class);
        if(ydPaypointShopInfoMapper.updateByPrimaryKey(shopInfo)<=0){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"更新失败，请刷新页面后重试!");
        }

        result.put(Constant.ORDERID,orderId);
        result.put(Constant.STATUS,orderDTO.getStatus());
        if(totalCount>0){
            //需要创建支付订单
            YeePayRequestDO yeePayRequestDO = new YeePayRequestDO();
            /**
             * 因为是公众号，所以此参数传7,identityid传openid
             */
            Map<String,Object> setMeal = getSetMeal(setMealType);
            yeePayRequestDO.setOrderid(orderId);
            yeePayRequestDO.setAmount(totalCount);
            yeePayRequestDO.setProductname(Constant.APPNAMECP+"-"+setMeal.get("name"));
            yeePayRequestDO.setProductdesc("购买时长："+monthNum.toString()+"个月");
            yeePayRequestDO.setIdentitytype(7);
            yeePayRequestDO.setIdentityid(openid);
            yeePayRequestDO.setAppId(userinfoService.getWeiXinAppIdForCP());
            yeePayRequestDO.setUserip(ip);
            YdPayResponse payResponse = ydPayService.payMobileRequest(yeePayRequestDO, PayOrderTypeEnum.CPORDER, DiamondYdSystemConfigHolder.getInstance().yeePayFCallbackUrl);
            if(payResponse.getSuccess()){
                result.put(Constant.PAYURL,payResponse.getPayurl());
            }else{
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"创建订单失败，请稍候再试！");
            }
        }

        if(cpOrder.getStatus().intValue()==OrderStatusEnum.NEW.getStatus().intValue()){
            /**
             * 需要发送超时关闭消息
              */
            /**
             * 以下发消息去关闭超30分钟没有支付的订单
             */
            Map<String,String> closeOrderMessage = new HashMap<>();
            closeOrderMessage.put(Constant.ORDERID,cpOrder.getOrderid());
            closeOrderMessage.put(Constant.PAYORDERTYPE,PayOrderTypeEnum.CPORDER.getName());
            closeOrderMessage.put(Constant.ORDERTYPE, OrderStatusEnum.OVER.getName());
            String msgId = mqMessageService.sendOrderStatusChange(cpOrder.getOrderid()+"applyclose",JSON.toJSONString(closeOrderMessage),1800000,PayOrderTypeEnum.CPORDER);
            logger.info("CpApply msgId is :"+msgId);
        }

        return result;
    }

    @Override
    public String getPayUrlBySetMeal(String openid, String shopid, String orderId) {
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCpOrderStatus(String orderid, OrderStatusEnum statusEnum) {
        try {
            YdPaypointCpOrder cpOrderInfo = ydPaypointCpOrderMapper.selectByOrderId(orderid);
            if (cpOrderInfo == null) {
                return;
            }

            if (statusEnum == OrderStatusEnum.OVER) {
                /**
                 * 关闭超时订单
                 */
                YdPaypointCpOrder cpOrderInfoOver = ydPaypointCpOrderMapper.selectByOrderIdRowLock(orderid);
                if (cpOrderInfoOver.getStatus().intValue() == OrderStatusEnum.NEW.getStatus().intValue()) {
                    cpOrderInfoOver.setStatus(OrderStatusEnum.OVER.getStatus());
                    if (ydPaypointCpOrderMapper.updateByPrimaryKey(cpOrderInfoOver) <= 0) {
                        throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "关闭引灯CP订单失败，订单ID：" + orderid);
                    }
                }
                return;
            }
            String shopid = cpOrderInfo.getShopid();

            YdPaypointShopInfo shopInfo = ydPaypointShopInfoMapper.selectByShopIdRowLock(shopid);
            CPOrderDTO cpOrderDTO = doMapper.map(cpOrderInfo, CPOrderDTO.class);
            if (StringUtil.isNotBlank(cpOrderDTO.getFeature(Constant.SETMEAL_BEGINDATE))) {
                /**
                 * 说明是套餐订单，这时再判断一下店铺状态是不是SETMEAL，如果是就要更新一下订单状态为NORMAL
                 */
                if (shopInfo != null) {
                    if (ShopStatusEnum.nameOf(shopInfo.getStatus()) == ShopStatusEnum.SETMEAL) {
                        ShopInfoDTO shopInfoDTO = doMapper.map(shopInfo, ShopInfoDTO.class);
                        shopInfoDTO.addFeature(Constant.SETMEAL_AMOUNT, AmountUtils.bigDecimal2Str(cpOrderDTO.getTotalAmount()));
                        shopInfo.setStatus(ShopStatusEnum.NORMAL.getType());
                        shopInfo.setFeature(shopInfoDTO.getFeature());
                        if (ydPaypointShopInfoMapper.updateByPrimaryKey(shopInfo) <= 0) {
                            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "更新店铺套餐信息失败，订单ID：" + orderid);
                        }
                        YdPaypointCpuserInfo cpuserInfo = ydPaypointCpuserInfoMapper.selectByOpenid(cpOrderInfo.getOpenid());
                        /**
                         * 还要查出是哪个用户的订单，并将此用户的默认店铺设置为这个shopid
                         */
                        if (cpuserInfo != null) {
                            CpUserInfoDTO cpUserInfoDTO = doMapper.map(cpuserInfo, CpUserInfoDTO.class);
                            cpUserInfoDTO.addFeature(Constant.DEFAULT_SHOP, shopid);
                            cpOrderInfo.setFeature(cpUserInfoDTO.getFeature());
                            if (ydPaypointCpuserInfoMapper.updateByPrimaryKeySelective(cpuserInfo) <= 0) {
                                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "更新CP订单状态失败，订单ID：" + orderid);
                            }
                        }

                    }
                }
                cpOrderInfo.setStatus(OrderStatusEnum.CONFIRM.getStatus());

            } else {
                /**
                 * 商城订单，有可能套餐升级与加油包，所以要更新店铺套餐类型与加油包订单数量
                 */
                ShopInfoDTO shopInfoDTO = doMapper.map(shopInfo, ShopInfoDTO.class);
                if (StringUtil.isNotBlank(cpOrderDTO.getFeature(Constant.UPSETMEAL))) {
                    shopInfoDTO.addFeature(Constant.SHOP_SETMEAL_TYPE, cpOrderDTO.getFeature(Constant.UPSETMEAL));
                    BigDecimal oldAmount;
                    if (StringUtil.isNotBlank(shopInfoDTO.getFeature(Constant.SETMEAL_AMOUNT))) {
                        oldAmount = new BigDecimal(shopInfoDTO.getFeature(Constant.SETMEAL_AMOUNT).trim());

                    } else {
                        oldAmount = new BigDecimal("0.00");
                    }
                    BigDecimal upAmount;
                    if (StringUtil.isNotBlank(cpOrderDTO.getFeature(Constant.UPSETMEAL_AMOUNT))) {
                        upAmount = new BigDecimal(cpOrderDTO.getFeature(Constant.UPSETMEAL_AMOUNT).trim());
                    } else {
                        upAmount = new BigDecimal("0.00");
                    }
                    BigDecimal newAmount = AmountUtils.add(oldAmount, upAmount);
                    shopInfoDTO.addFeature(Constant.SETMEAL_AMOUNT, AmountUtils.bigDecimal2Str(newAmount));
                }
                if (StringUtil.isNotBlank(cpOrderDTO.getFeature(Constant.BAGORDERNUM))) {
                    String curBagNum = shopInfoDTO.getFeature(Constant.BAGORDERNUM);
                    Integer bagNum = 0;
                    Integer orderBagNum = Integer.valueOf(cpOrderDTO.getFeature(Constant.BAGORDERNUM).trim());
                    if (StringUtil.isNotBlank(curBagNum)) {
                        bagNum = Integer.valueOf(curBagNum.trim());
                    }
                    bagNum = bagNum + orderBagNum;
                    shopInfoDTO.addFeature(Constant.BAGORDERNUM, bagNum.toString());
                }
                if (StringUtil.isNotBlank(cpOrderDTO.getFeature(Constant.UPSETMEAL)) || StringUtil.isNotBlank(cpOrderDTO.getFeature(Constant.BAGORDERNUM))) {
                    shopInfo.setFeature(shopInfoDTO.getFeature());
                    if (ydPaypointShopInfoMapper.updateByPrimaryKeySelective(shopInfo) <= 0) {
                        logger.error("更新CP订单状态失败，订单ID：" + orderid);
                        throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "更新CP订单状态失败，订单ID：" + orderid);
                    }
                }

                cpOrderInfo.setStatus(statusEnum.getStatus());

            }
            /**
             * 更新状态完成
             */
            if (ydPaypointCpOrderMapper.updateByPrimaryKey(cpOrderInfo) <= 0) {
                logger.error("更新CP订单状态失败，订单ID：" + orderid);
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "更新CP订单状态失败，订单ID：" + orderid);
            }
        }catch (Exception e){
            logger.error("updateCpOrderStatus is error: ",e);
            throw new YdException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> queryOrderIdByCP(String openid, String orderid) throws Exception {
        List<String> shops = getShopsByManager(openid);
        if(shops==null){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        YdPaypointCpOrder cpOrderInfo = ydPaypointCpOrderMapper.selectByOrderId(orderid);
        if(cpOrderInfo==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"订单不存在!");
        }
        String shopid = cpOrderInfo.getShopid();
        if(!shops.contains(shopid.trim())){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),"只有管理员才能操作!");
        }
        if((cpOrderInfo.getTotalAmount().toBigInteger().intValue()==0)&&(OrderStatusEnum.nameOf(cpOrderInfo.getStatus())==OrderStatusEnum.CONFIRM)){
            /**
             * 说明是0元订单
             */
            Map<String,Object> result = new HashMap<>();
            result.put("success", true);
            result.put("status", 1);
            result.put("amount", 0);
            result.put("orderid", orderid);
            result.put("shopid",cpOrderInfo.getShopid());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            result.put("ordertime", sdf.format(cpOrderInfo.getCreateDate()));
            return result;
        }
        Map<String,Object> result = ydPayService.queryPayOrderInfoByLocal(orderid,PayOrderTypeEnum.CPORDER,OrderStatusEnum.PAYFINISH);
        result.put("shopid",cpOrderInfo.getShopid());
        return result;

    }

    @Override
    public Long queryRemanent(String shopid,boolean isDay) {
        YdPaypointShopInfo shopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
        if(shopInfo!=null){
            ShopInfoDTO shopInfoDTO = doMapper.map(shopInfo,ShopInfoDTO.class);
            return this.queryRemanent(shopInfoDTO,isDay);
        }
        return 0L;
    }

    @Override
    public Long queryRemanent(ShopInfoDTO shopInfoDTO,boolean isDay) {
        if(shopInfoDTO!=null){
            if(shopInfoDTO.getStatus()>2) {
                String beginStr = DateUtils.getToday();
                String endStr = DateUtils.getDate(DateUtils.date2LocalDate(shopInfoDTO.getContractTimeEnd()));
                if (StringUtil.isNotBlank(beginStr) && StringUtil.isNotBlank(endStr)) {
                    if(isDay) {
                        Long rem = DateUtils.subDate(beginStr, endStr);
                        if (rem <= 0) {
                            return 0L;
                        }
                        return rem;
                    }else {
                        Long rem = DateUtils.subMonth(beginStr, endStr);
                        if (rem <= 0) {
                            return 0L;
                        }
                        return rem;
                    }
                }
            }
        }
        return 0L;
    }

    @Override
    public Map<String, Long> queryRemanent(String shopid) {
        YdPaypointShopInfo shopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
        if(shopInfo!=null){
            ShopInfoDTO shopInfoDTO = doMapper.map(shopInfo,ShopInfoDTO.class);
            if(shopInfoDTO!=null){
                if(shopInfoDTO.getStatus()>2) {
                    String beginStr = DateUtils.getToday();
                    String endStr = DateUtils.getDate(DateUtils.date2LocalDate(shopInfoDTO.getContractTimeEnd()));
                    if (StringUtil.isNotBlank(beginStr) && StringUtil.isNotBlank(endStr)) {
                        return DateUtils.subMonthAndDay(beginStr,endStr);
                    }
                }
            }
        }
        return null;
    }


    @Override
    public Map<String,Object> getSetMeal(String setMealType){
        Map<String, Object> setMeal = null;
        if(Constant.FREE_VERSION.toLowerCase().equals(setMealType.trim().toLowerCase())){
            setMeal = JSON.parseObject(DiamondYdPayPointConfigHolder.getInstance().freeV,Map.class);
        }
        if(Constant.BASIC_VERSION.toLowerCase().equals(setMealType.trim().toLowerCase())){
            setMeal = JSON.parseObject(DiamondYdPayPointConfigHolder.getInstance().basicV,Map.class);
        }
        if(Constant.PROFESSIONAL_VAERSION.toLowerCase().equals(setMealType.trim().toLowerCase())){
            setMeal = JSON.parseObject(DiamondYdPayPointConfigHolder.getInstance().professionalV,Map.class);
        }
        if(Constant.GOLD_VERSION.toLowerCase().equals(setMealType.trim().toLowerCase())){
            setMeal = JSON.parseObject(DiamondYdPayPointConfigHolder.getInstance().goldV,Map.class);
        }
        if(Constant.DIAMOND_VERSION.toLowerCase().equals(setMealType.trim().toLowerCase())){
            setMeal = JSON.parseObject(DiamondYdPayPointConfigHolder.getInstance().diamondV,Map.class);
        }
        if(setMeal==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),ErrorCodeConstants.PARAM_ERROR.getErrorMessage());
        }
        return setMeal;
    }

    @Override
    public ApplyShopInfoVO getApplyingShopInfo(String openid) {
        List<String> shopIds = this.getShopsByOwner(openid);
        if(shopIds==null||shopIds.size()<=0){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"店铺不存在!");
        }
        for(String shopid:shopIds){
            ApplyShopInfoVO shopInfoVO = this.getApplyShopInfoByShopId(shopid);
            if(shopInfoVO.getShopStatus()==0||shopInfoVO.getShopStatus()==1){
                return shopInfoVO;
            }
        }
        return null;
    }

    @Override
    public boolean checkDeviceInShop(String shopid,String deviceid, TypeEnum type) {
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean setPayModel(String openid, String shopid, boolean isPayNow) {

        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        YdPaypointShopInfo shopInfo = ydPaypointShopInfoMapper.selectByShopIdRowLock(shopid);
        if(shopInfo==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"店铺不存在!");
        }
        ShopInfoDTO shopInfoDTO = doMapper.map(shopInfo,ShopInfoDTO.class);
        if(isPayNow) {
            if (shopInfoDTO.isPayNow()) {
                return true;
            }
            shopInfoDTO.addFlag(ShopSupportFlagConstants.PAYNOW);
            shopInfo.setFlag(shopInfoDTO.getFlag());
        }else{
            if (!shopInfoDTO.isPayNow()) {
                return true;
            }
            shopInfoDTO.removeFlag(ShopSupportFlagConstants.PAYNOW);
            shopInfo.setFlag(shopInfoDTO.getFlag());
        }
        if(ydPaypointShopInfoMapper.updateByPrimaryKeySelective(shopInfo)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"设置失败!");
        }
        return true;
    }

    @Override
    public boolean isPayNow(String shopid) {
        YdPaypointShopInfo shopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
        if(shopInfo==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"店铺不存在!");
        }
        ShopInfoDTO shopInfoDTO = doMapper.map(shopInfo,ShopInfoDTO.class);
        return shopInfoDTO.isPayNow();
    }

    @Override
    public boolean setUserPayModel(String openid, String shopid, boolean isPayNow) {
        if(!userinfoService.checkIsManager(openid,shopid)){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }

        YdPaypointShopInfo shopInfo = ydPaypointShopInfoMapper.selectByShopIdRowLock(shopid);
        if(shopInfo==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"店铺不存在!");
        }
        ShopInfoDTO shopInfoDTO = doMapper.map(shopInfo,ShopInfoDTO.class);
        if(isPayNow) {
            if (shopInfoDTO.userPayNow()) {
                return true;
            }
            shopInfoDTO.addFlag(ShopSupportFlagConstants.PAYNOW2C);
            shopInfo.setFlag(shopInfoDTO.getFlag());
        }else{
            if (!shopInfoDTO.userPayNow()) {
                return true;
            }
            shopInfoDTO.removeFlag(ShopSupportFlagConstants.PAYNOW2C);
            shopInfo.setFlag(shopInfoDTO.getFlag());
        }
        if(ydPaypointShopInfoMapper.updateByPrimaryKeySelective(shopInfo)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"设置失败!");
        }
        return true;
    }

    @Override
    public boolean userPayNow(String shopid) {
        YdPaypointShopInfo shopInfo = ydPaypointShopInfoMapper.selectByShopId(shopid);
        if(shopInfo==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"店铺不存在!");
        }
        ShopInfoDTO shopInfoDTO = doMapper.map(shopInfo,ShopInfoDTO.class);
        return shopInfoDTO.userPayNow();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean initMemberCard(String shopid) {
        /**
         * 加分布式事务缓存锁,超时时间为2.5秒
         */
        boolean lockIsTrue = redisManager.lockWithTimeout(shopid+"initcard",shopid,3000,2500);
        List<YdCpMemberCard> ydCpMemberCards = ydCpMemberCardMapper.selectByShopid(shopid);
        if(ydCpMemberCards==null||ydCpMemberCards.size()<=0){

            /**
             * level1
             */
            MemberCardTypeEnum cardTypeEnum = MemberCardTypeEnum.nameOf(0);
            YdCpMemberCard cpMemberCard = new YdCpMemberCard();
            cpMemberCard.setShopid(shopid);
            cpMemberCard.setLevelType(cardTypeEnum.getType());
            cpMemberCard.setLevelName(cardTypeEnum.getDesc());
            cpMemberCard.setCardid(RandomUtil.getSNCode(TypeEnum.MEMBERCARD));
            /**初始10元消费能积10分**/
            cpMemberCard.setAccMoney(1000);
            cpMemberCard.setAccPoint(10);
            /**初始1000分抵扣1毛钱**/
            cpMemberCard.setDeductionMoney(10);
            cpMemberCard.setDeductionPoint(1000);
            cpMemberCard.setMinPoint(0);
            cpMemberCard.setMaxPoint(999999);
            cpMemberCard.setUserCount(0L);
            cpMemberCard.setPayUserCount(0L);
            if(ydCpMemberCardMapper.insert(cpMemberCard)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"初始化失败!");
            }

            /**
             * level2
             */
            cardTypeEnum = MemberCardTypeEnum.nameOf(1);
            cpMemberCard = new YdCpMemberCard();
            cpMemberCard.setShopid(shopid);
            cpMemberCard.setLevelType(cardTypeEnum.getType());
            cpMemberCard.setLevelName(cardTypeEnum.getDesc());
            cpMemberCard.setCardid(RandomUtil.getSNCode(TypeEnum.MEMBERCARD));
            cpMemberCard.setAccMoney(1000);
            cpMemberCard.setAccPoint(20);
            cpMemberCard.setDeductionMoney(20);
            cpMemberCard.setDeductionPoint(1000);
            cpMemberCard.setMinPoint(1000000);
            cpMemberCard.setMaxPoint(9999999);
            cpMemberCard.setUserCount(0L);
            cpMemberCard.setPayUserCount(0L);
            if(ydCpMemberCardMapper.insert(cpMemberCard)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"初始化失败!");
            }

            /**
             * level3
             */
            cardTypeEnum = MemberCardTypeEnum.nameOf(2);
            cpMemberCard = new YdCpMemberCard();
            cpMemberCard.setShopid(shopid);
            cpMemberCard.setLevelType(cardTypeEnum.getType());
            cpMemberCard.setLevelName(cardTypeEnum.getDesc());
            cpMemberCard.setCardid(RandomUtil.getSNCode(TypeEnum.MEMBERCARD));
            cpMemberCard.setAccMoney(1000);
            cpMemberCard.setAccPoint(50);
            cpMemberCard.setDeductionMoney(50);
            cpMemberCard.setDeductionPoint(1000);
            cpMemberCard.setMinPoint(10000000);
            cpMemberCard.setMaxPoint(99999999);
            cpMemberCard.setUserCount(0L);
            cpMemberCard.setPayUserCount(0L);
            if(ydCpMemberCardMapper.insert(cpMemberCard)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"初始化失败!");
            }

            /**
             * level4
             */
            cardTypeEnum = MemberCardTypeEnum.nameOf(3);
            cpMemberCard = new YdCpMemberCard();
            cpMemberCard.setShopid(shopid);
            cpMemberCard.setLevelType(cardTypeEnum.getType());
            cpMemberCard.setLevelName(cardTypeEnum.getDesc());
            cpMemberCard.setCardid(RandomUtil.getSNCode(TypeEnum.MEMBERCARD));
            cpMemberCard.setAccMoney(1000);
            cpMemberCard.setAccPoint(80);
            cpMemberCard.setDeductionMoney(80);
            cpMemberCard.setDeductionPoint(1000);
            cpMemberCard.setMinPoint(100000000);
            cpMemberCard.setMaxPoint(999999999);
            cpMemberCard.setUserCount(0L);
            cpMemberCard.setPayUserCount(0L);
            if(ydCpMemberCardMapper.insert(cpMemberCard)<=0){
                throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"初始化失败!");
            }



        }else {
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"此店铺已经进行过会员卡功能初始化!");
        }
        if(lockIsTrue){
            /**
             * 释放锁
             */
            redisManager.releaseLock(shopid+"initcard",shopid);
        }
        return true;
    }

    @Override
    public List<YdCpMemberCardVO> queryMemberCardInfoList(String shopid) {
        List<YdCpMemberCard> ydCpMemberCards = ydCpMemberCardMapper.selectByShopid(shopid);
        if(ydCpMemberCards==null||ydCpMemberCards.size()<=0){
            boolean isok = this.initMemberCard(shopid);
            if(!isok){
                return null;
            }
            ydCpMemberCards = ydCpMemberCardMapper.selectByShopid(shopid);
            if(ydCpMemberCards==null){
                return null;
            }
        }
        List<YdCpMemberCardVO> ydCpMemberCardVOS = new ArrayList<>();
        for(YdCpMemberCard ydCpMemberCard : ydCpMemberCards){
            YdCpMemberCardVO ydCpMemberCardVO = doMapper.map(ydCpMemberCard,YdCpMemberCardVO.class);
            ydCpMemberCardVO.setDeductionMoney(AmountUtils.changeF2YWithBigDecimal(ydCpMemberCard.getDeductionMoney()));
            ydCpMemberCardVO.setAccMoney(AmountUtils.changeF2YWithBigDecimal(ydCpMemberCard.getAccMoney()));
            ydCpMemberCardVO.setPicUrl(MemberCardTypeEnum.nameOf(ydCpMemberCard.getLevelType()).getPicUrl());
            ydCpMemberCardVOS.add(ydCpMemberCardVO);
        }

        return  ydCpMemberCardVOS;
    }

    @Override
    public YdCpMemberCardVO queryMemberCardInfo(String cardid) {
        YdCpMemberCard ydCpMemberCard = ydCpMemberCardMapper.selectByCradid(cardid);
        if(ydCpMemberCard==null){
            return null;
        }
        YdCpMemberCardVO ydCpMemberCardVO = doMapper.map(ydCpMemberCard,YdCpMemberCardVO.class);
        ydCpMemberCardVO.setDeductionMoney(AmountUtils.changeF2YWithBigDecimal(ydCpMemberCard.getDeductionMoney()));
        ydCpMemberCardVO.setAccMoney(AmountUtils.changeF2YWithBigDecimal(ydCpMemberCard.getAccMoney()));
        ydCpMemberCardVO.setPicUrl(MemberCardTypeEnum.nameOf(ydCpMemberCard.getLevelType()).getPicUrl());
        return ydCpMemberCardVO;

    }

    @Override
    public boolean updateMemberCardInfo(String openid, YdCpMemberCardVO cpMemberCardVO) {
        if(StringUtil.isEmpty(cpMemberCardVO.getCardid())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"会员卡id不能为空!");
        }
        if(!(userinfoService.checkIsOwner(openid,cpMemberCardVO.getShopid())||userinfoService.checkIsManager(openid,cpMemberCardVO.getShopid()))){
            throw new YdException(ErrorCodeConstants.PASSPORT_ERROR.getErrorCode(),ErrorCodeConstants.PASSPORT_ERROR.getErrorMessage());
        }
        logger.info("cpMemberCardVO is :");
        Integer accMoney = AmountUtils.changeY2F(cpMemberCardVO.getAccMoney());
        Integer deductionMoney = AmountUtils.changeY2F(cpMemberCardVO.getDeductionMoney());
        YdCpMemberCard ydCpMemberCard = ydCpMemberCardMapper.selectByCradid(cpMemberCardVO.getCardid());
        if(ydCpMemberCard==null){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(),"会员卡基本设置信息不存在!");
        }
        if(cpMemberCardVO.getDeductionPoint().intValue()<=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"可抵扣积分设置不能为0!");
        }
        if(deductionMoney.intValue()<=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"可抵扣金额设置不能为0!");
        }

        if(accMoney.intValue()<=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"可奖励金额设置不能为0!");
        }
        if(cpMemberCardVO.getAccPoint().intValue()<=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"可奖励积分设置不能为0!");
        }

        ydCpMemberCard.setMinPoint(cpMemberCardVO.getMinPoint());
        ydCpMemberCard.setMaxPoint(cpMemberCardVO.getMaxPoint());
        ydCpMemberCard.setDeductionPoint(cpMemberCardVO.getDeductionPoint());
        ydCpMemberCard.setDeductionMoney(deductionMoney);
        ydCpMemberCard.setAccPoint(cpMemberCardVO.getAccPoint());
        ydCpMemberCard.setAccMoney(accMoney);
        ydCpMemberCard.setLevelName(cpMemberCardVO.getLevelName());

        if(ydCpMemberCardMapper.updateByPrimaryKeySelective(ydCpMemberCard)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(),"更新失败!");
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer rewardConsumptionPoint(Long userid, Integer userSourceType, String shopid, BigDecimal amount,boolean writeDB) {
        YdShopUserData shopUserData = ydShopUserDataMapper.selectByUserIdRowLock(userid,userSourceType,shopid);
        Integer point = 0;
        Long totalPoint = 0L;
        if(shopUserData!=null){
            totalPoint = shopUserData.getPointTotal();
        }

        List<YdCpMemberCardVO> cpMemberCardVOS = this.queryMemberCardInfoList(shopid);
        if(cpMemberCardVOS==null||cpMemberCardVOS.size()<=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "店铺没有配置会员卡设置!");
        }

        for(YdCpMemberCardVO memberCardVO: cpMemberCardVOS){
            if(totalPoint>=memberCardVO.getMinPoint()&&totalPoint<=memberCardVO.getMaxPoint()){

                Integer money = AmountUtils.changeY2F(amount);

                Integer value = money / AmountUtils.changeY2F(memberCardVO.getAccMoney());

                point = value * memberCardVO.getAccPoint();
                if(!writeDB){
                    return point;
                }
                if(shopUserData==null){
                    shopUserData = new YdShopUserData();
                    shopUserData.setShopid(shopid);
                    shopUserData.setUserid(userid);
                    shopUserData.setUserSource(userSourceType);
                    shopUserData.setPointTotal(Long.valueOf(point.toString()));
                    shopUserData.setCurPoint(shopUserData.getPointTotal());
                    shopUserData.setAmountTotal(amount);
                    shopUserData.setLastEnterShopTime(new Date());
                    if(ydShopUserDataMapper.insert(shopUserData)<=0){
                        throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "积分奖励失败!");
                    }
                }else {

                    shopUserData.setLastEnterShopTime(new Date());
                    shopUserData.setAmountTotal(AmountUtils.add(shopUserData.getAmountTotal(),amount));
                    shopUserData.setPointTotal(shopUserData.getPointTotal()+point);
                    shopUserData.setCurPoint(shopUserData.getCurPoint()+point);
                    if(ydShopUserDataMapper.updateByPrimaryKeySelective(shopUserData)<=0){
                        throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "积分奖励失败!");
                    }

                }
                break;
            }
        }


        return point;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BigDecimal consumptionPoint(Long userid, Integer userSourceType, String shopid, Integer point, BigDecimal orderMoney) throws IllegalAccessException {
        YdShopUserData shopUserData = ydShopUserDataMapper.selectByUserIdRowLock(userid,userSourceType,shopid);
        if(shopUserData==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "用户积分不足!");
        }
        if(shopUserData.getCurPoint().intValue()<point.intValue()){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "用户积分不足!");
        }
        List<YdCpMemberCardVO> cpMemberCardVOS = this.queryMemberCardInfoList(shopid);
        if(cpMemberCardVOS==null||cpMemberCardVOS.size()<=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "店铺没有配置会员卡设置!");
        }

        /**
         * 检查抵扣金额是否正确
         */

        BigDecimal moneyTmp = null;
        if(cpMemberCardVOS!=null) {
            for (YdCpMemberCardVO memberCardVO : cpMemberCardVOS) {
                if (shopUserData.getPointTotal().longValue() >= memberCardVO.getMinPoint().intValue() && shopUserData.getPointTotal().longValue() <= memberCardVO.getMaxPoint().intValue()) {
                    BigDecimal pointTmp = AmountUtils.div(point, memberCardVO.getDeductionPoint(), 0);
                    moneyTmp = AmountUtils.mul(pointTmp, memberCardVO.getDeductionMoney());
                    if (AmountUtils.changeY2F(moneyTmp) >= AmountUtils.changeY2F(orderMoney)) {
                        throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "积分能抵扣的金额超出了订单金额!");
                    }

                    /**
                     * 开始扣除积分
                     */
                    long targetPoint = shopUserData.getCurPoint().longValue() - point;
                    if (targetPoint < 0) {
                        targetPoint = 0L;
                    }
                    shopUserData.setCurPoint(targetPoint);
                    if (ydShopUserDataMapper.updateByPrimaryKeySelective(shopUserData) <= 0) {
                        throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "更新用户积分失败!");
                    }

                    break;
                }
            }
        }

        if(moneyTmp==null){
            /**
             * 当一个用户的积分累积到比店铺最高等级会员设置的最大积分还要大时，需要把用户当成最高等级使用积分
             */
            if(cpMemberCardVOS!=null){
                YdCpMemberCardVO memberCardVO = cpMemberCardVOS.get(cpMemberCardVOS.size()-1);
                if (shopUserData.getPointTotal().longValue() > memberCardVO.getMaxPoint().intValue()){
                    BigDecimal pointTmp = AmountUtils.div(point, memberCardVO.getDeductionPoint(), 0);
                    moneyTmp = AmountUtils.mul(pointTmp, memberCardVO.getDeductionMoney());
                    if (AmountUtils.changeY2F(moneyTmp) >= AmountUtils.changeY2F(orderMoney)) {
                        throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "积分能抵扣的金额超出了订单金额!");
                    }

                    /**
                     * 开始扣除积分
                     */
                    long targetPoint = shopUserData.getCurPoint().longValue() - point;
                    if (targetPoint < 0) {
                        targetPoint = 0L;
                    }
                    shopUserData.setCurPoint(targetPoint);
                    if (ydShopUserDataMapper.updateByPrimaryKeySelective(shopUserData) <= 0) {
                        throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "更新用户积分失败!");
                    }
                }
            }
        }


        return moneyTmp;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean rebackConsumptionPoint(Long userid, Integer userSourceType, String shopid, Integer point) {
        YdShopUserData shopUserData = ydShopUserDataMapper.selectByUserIdRowLock(userid,userSourceType,shopid);
        if(shopUserData==null){
            return true;
        }

        shopUserData.setCurPoint(shopUserData.getCurPoint()+point);
        if(ydShopUserDataMapper.updateByPrimaryKeySelective(shopUserData)<=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "积分返还失败!");
        }

        return true;
    }

    @Override
    public Map<String, Object> getDeductionMoney(Long userid, Integer userSourceType, String shopid,BigDecimal orderMoney) throws IllegalAccessException {
        Map<String, Object> result =  new HashMap<>();
        result.put(Constant.POINT,0);
        result.put(Constant.MONEY,new BigDecimal("0.00"));
        YdShopUserData shopUserData = ydShopUserDataMapper.selectByUserId(userid,userSourceType,shopid);
        if(shopUserData!=null){
            Integer point = shopUserData.getCurPoint().intValue();
            List<YdCpMemberCardVO> cpMemberCardVOS = this.queryMemberCardInfoList(shopid);
            if(!(cpMemberCardVOS==null||cpMemberCardVOS.size()<=0)){
                for(YdCpMemberCardVO memberCardVO: cpMemberCardVOS) {
                    if (shopUserData.getPointTotal().longValue() >= memberCardVO.getMinPoint().intValue() && shopUserData.getPointTotal().longValue() <= memberCardVO.getMaxPoint().intValue()) {
                        BigDecimal pointTmp = AmountUtils.div(point,memberCardVO.getDeductionPoint(),0);
                        BigDecimal moneyTmp = AmountUtils.mul(pointTmp,memberCardVO.getDeductionMoney());
                        while (AmountUtils.changeY2F(moneyTmp)>=AmountUtils.changeY2F(orderMoney)){
                            point = point / 2;
                            pointTmp = AmountUtils.div(point,memberCardVO.getDeductionPoint(),0);
                            moneyTmp = AmountUtils.mul(pointTmp,memberCardVO.getDeductionMoney());
                        }
                        result.put(Constant.POINT,point);
                        result.put(Constant.MONEY,moneyTmp);
                        break;
                    }

                }

                /**
                 * 如果总的积分，比最后一个等级的会员级别的最大分值还要大，就需要把它当作最后一个等级来使用积分
                 */
                YdCpMemberCardVO ydCpMemberCardVO = cpMemberCardVOS.get(cpMemberCardVOS.size()-1);
                if (shopUserData.getPointTotal().longValue() > ydCpMemberCardVO.getMaxPoint().intValue()){
                    BigDecimal pointTmp = AmountUtils.div(point,ydCpMemberCardVO.getDeductionPoint(),0);
                    BigDecimal moneyTmp = AmountUtils.mul(pointTmp,ydCpMemberCardVO.getDeductionMoney());
                    while (AmountUtils.changeY2F(moneyTmp)>=AmountUtils.changeY2F(orderMoney)){
                        point = point / 2;
                        pointTmp = AmountUtils.div(point,ydCpMemberCardVO.getDeductionPoint(),0);
                        moneyTmp = AmountUtils.mul(pointTmp,ydCpMemberCardVO.getDeductionMoney());
                    }
                    result.put(Constant.POINT,point);
                    result.put(Constant.MONEY,moneyTmp);
                }

            }

        }

        return result;
    }

    @Override
    public Map<String, Long> getCardPointValue(Long userid, Integer userSourceType, String shopid) {
        Map<String, Long> result = new HashMap<>();
        YdShopUserData shopUserData = ydShopUserDataMapper.selectByUserIdRowLock(userid,userSourceType,shopid);
        if(shopUserData==null){
            result.put(Constant.POINT,0L);
            result.put(Constant.POINTTOTAL,0L);
            return result;
        }
        result.put(Constant.POINTTOTAL,shopUserData.getPointTotal());
        result.put(Constant.POINT,shopUserData.getCurPoint());
        return result;

    }

    @Override
    public boolean shopIsSleep(YdShopHoursInfoVO shopHoursInfoVO,Integer status){
        boolean result = false;

        if(status.intValue()==4){
            return true;
        }

        LocalDate now = LocalDate.now();
        Integer dayInt = now.getDayOfWeek().getValue();

        if(dayInt.intValue()>=shopHoursInfoVO.getBeginDayofweek().intValue()&&
                dayInt.intValue()<=shopHoursInfoVO.getEndDayofweek().intValue()){

            LocalDateTime time = LocalDateTime.now();
            int curH = time.getHour();
            int curM = time.getMinute();
            if(curH<shopHoursInfoVO.getEndH().intValue()&&curH>=shopHoursInfoVO.getBeginH().intValue()){
                result = true;
            }else if(curH==shopHoursInfoVO.getEndH().intValue()){
                if(curM<shopHoursInfoVO.getEndM().intValue()){
                    result = true;
                }

            }

        }

        return result;
    }


//    public static void main(String[] args)  {
//
//        Integer i= 2;
//        PayTypeEnum payTypeEnum = PayTypeEnum.nameOf("yeepay");
//        PayTypeEnum payTypeEnum1 = PayTypeEnum.nameOf(1);
//        System.out.println(payTypeEnum);
//        System.out.println(payTypeEnum1);
//
//    }

}
