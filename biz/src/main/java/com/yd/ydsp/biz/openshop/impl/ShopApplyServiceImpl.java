package com.yd.ydsp.biz.openshop.impl;

import com.yd.ydsp.biz.config.DiamondWeiXinInfoConfigHolder;
import com.yd.ydsp.biz.manage.CpAgentService;
import com.yd.ydsp.biz.openshop.ShopApplyService;
import com.yd.ydsp.client.domian.ShopInfoDTO;
import com.yd.ydsp.client.domian.manage.YdAgentInfoDTO;
import com.yd.ydsp.client.domian.paypoint.CpUserInfoDTO;
import com.yd.ydsp.common.Exception.YdException;
import com.yd.ydsp.common.constants.Constant;
import com.yd.ydsp.common.constants.ErrorCodeConstants;
import com.yd.ydsp.common.enums.DeliveryTypeEnum;
import com.yd.ydsp.common.enums.manage.ShopApplyStatusEnum;
import com.yd.ydsp.common.enums.paypoint.ShopStatusEnum;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.redis.RedisManager;
import com.yd.ydsp.common.redis.SerializeUtils;
import com.yd.ydsp.common.utils.RandomUtil;
import com.yd.ydsp.dal.entity.*;
import com.yd.ydsp.dal.mapper.*;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;

public class ShopApplyServiceImpl implements ShopApplyService {

    public static final Logger logger = LoggerFactory.getLogger(ShopApplyServiceImpl.class);

    @Resource
    RedisManager redisManager;
    @Resource
    private DozerBeanMapper doMapper;

    @Resource
    private YdShopApplyInfoMapper ydShopApplyInfoMapper;
    @Resource
    private YdPaypointShopInfoMapper ydPaypointShopInfoMapper;
    @Resource
    private YdPaypointShopInfoExtMapper ydPaypointShopInfoExtMapper;
    @Resource
    private YdManageUserInfoMapper ydManageUserInfoMapper;
    @Resource
    private CpAgentService cpAgentService;
    @Resource
    private YdPaypointCpuserInfoMapper ydPaypointCpuserInfoMapper;
    @Resource
    private YdPaypointShopworkerMapper ydPaypointShopworkerMapper;
    @Resource
    private YdAgentInfoMapper ydAgentInfoMapper;



    protected boolean createPreAuthLinkCode(String shopid,Integer weixinType) throws IOException {
        String applyidrem = shopid+weixinType;
        byte[] bToken = redisManager.get(SerializeUtils.serialize(applyidrem));
        if (bToken == null) {
            /**
             * 一小时有效
             */
            redisManager.set(SerializeUtils.serialize(applyidrem), SerializeUtils.serialize(RandomUtil.getSNCode(TypeEnum.PRELINKCODE)), DiamondWeiXinInfoConfigHolder.getInstance().getPreAuthLinkCodeExpin());
        }else {
            redisManager.del(SerializeUtils.serialize(applyidrem));
            redisManager.set(SerializeUtils.serialize(applyidrem), SerializeUtils.serialize(RandomUtil.getSNCode(TypeEnum.PRELINKCODE)), DiamondWeiXinInfoConfigHolder.getInstance().getPreAuthLinkCodeExpin());
        }
        return true;
    }


    protected String getPreAuthLinkCode(String shopid,Integer weixinType) throws IOException, ClassNotFoundException {
        String applyidrem = shopid+weixinType;
        byte[] bToken = redisManager.get(SerializeUtils.serialize(applyidrem));
        if (bToken != null) {
            return (String) SerializeUtils.deserialize(bToken);
        }
        return null;
    }

    @Override
    public String getPreAuthLink(String openid,String shopid,Integer weixinType,String appid) throws IOException, ClassNotFoundException {

        YdManageUserInfo ydManageUserInfo = ydManageUserInfoMapper.selectByOpenidLockRow(openid);
        if(ydManageUserInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"登录用户错误，请检查!");
        }
        YdPaypointShopInfoExt ydPaypointShopInfoExt = ydPaypointShopInfoExtMapper.selectByShopId(shopid);
        if(ydPaypointShopInfoExt==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "不存在的店铺！");
        }
        if(!cpAgentService.isAgentManager(ydManageUserInfo.getMobile(),ydPaypointShopInfoExt.getAgentid())){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(),"你无权进行此操作!");
        }
        if(!this.createPreAuthLinkCode(shopid,weixinType)){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "取授权链接失败！");
        }
        String authLinkCode = this.getPreAuthLinkCode(shopid,weixinType);


        return DiamondWeiXinInfoConfigHolder.getInstance().getShopApplyWeiXinPreAuthUrl()+appid+"/"+weixinType+"/"+shopid+"/"+authLinkCode;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean applyToCreateShop(String applyid) {
        boolean result = true;
        YdShopApplyInfo ydShopApplyInfo = ydShopApplyInfoMapper.selectByApplyidRowLock(applyid);
        if(ydShopApplyInfo==null){
            logger.error(applyid+"的店铺申请单并不存在!");
            return result;
        }

        /**
         * 修改申请单状态为完成
         */

        ydShopApplyInfo.setStatus(ShopApplyStatusEnum.FINISH.getStatus());
        if(ydShopApplyInfoMapper.updateByPrimaryKey(ydShopApplyInfo)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "修改申请单状态失败！");
        }

        /**
         * applyid成为shopid，所以使用applyid来查询店铺信息
         */
        YdPaypointShopInfo ydPaypointShopInfo = ydPaypointShopInfoMapper.selectByShopId(applyid);
        YdPaypointShopInfoExt ydPaypointShopInfoExt = ydPaypointShopInfoExtMapper.selectByShopId(applyid);
        if(ydPaypointShopInfo!=null||ydPaypointShopInfoExt!=null){
            logger.error("applyid is :"+applyid+" 已经完成了店铺创建，无法重复创建！");
            return result;
        }

        ydPaypointShopInfo = new YdPaypointShopInfo();
        ydPaypointShopInfo.setZipcode(ydShopApplyInfo.getZipcode());
        ydPaypointShopInfo.setDistrict(ydShopApplyInfo.getDistrict());
        ydPaypointShopInfo.setAddress(ydShopApplyInfo.getAddress());
        ydPaypointShopInfo.setCity(ydShopApplyInfo.getCity());
        ydPaypointShopInfo.setProvince(ydShopApplyInfo.getProvince());
        ydPaypointShopInfo.setCountry(ydShopApplyInfo.getCountry());
        ydPaypointShopInfo.setDescription(ydShopApplyInfo.getDescription());
        ydPaypointShopInfo.setTelephone(ydShopApplyInfo.getTelephone());
        ydPaypointShopInfo.setStatus(ShopStatusEnum.NORMAL.getType());
        ydPaypointShopInfo.setShopImg(ydShopApplyInfo.getShopImg());
        ydPaypointShopInfo.setName(ydShopApplyInfo.getName());
        ydPaypointShopInfo.setCorporation(ydShopApplyInfo.getCorporation());
        ydPaypointShopInfo.setCode(ydShopApplyInfo.getBusinessCode());
        ydPaypointShopInfo.setContact(ydShopApplyInfo.getContact());
        ydPaypointShopInfo.setContractTimeBegin(ydShopApplyInfo.getContractTimeBegin());
        ydPaypointShopInfo.setContractTimeEnd(ydShopApplyInfo.getContractTimeEnd());
        ydPaypointShopInfo.setEmail(ydShopApplyInfo.getEmail());
        ydPaypointShopInfo.setJointime(new Date());
        ydPaypointShopInfo.setMobile(ydShopApplyInfo.getMobile());
        ydPaypointShopInfo.setShopid(ydShopApplyInfo.getApplyid());
        ydPaypointShopInfo.setTrade(ydShopApplyInfo.getTradeType());
        /**
         * 设置店铺的feature
         */

        ShopInfoDTO shopInfoDTO = doMapper.map(ydPaypointShopInfo,ShopInfoDTO.class);
        shopInfoDTO.addFeature(Constant.IDENTITY_NUMBER,ydShopApplyInfo.getOwnerIdentificationCard());
        shopInfoDTO.addFeature(Constant.SHOP_SETMEAL_TYPE,Constant.DIAMOND_VERSION);
        shopInfoDTO.addFeature(Constant.DELIVERYTYPE, DeliveryTypeEnum.SHANGJIAPEI.getType().toString());

        ydPaypointShopInfo.setFeature(shopInfoDTO.getFeature());

        if(ydPaypointShopInfoMapper.insert(ydPaypointShopInfo)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "创建店铺失败！");
        }

        ydPaypointShopInfoExt = new YdPaypointShopInfoExt();
        ydPaypointShopInfoExt.setShopid(ydShopApplyInfo.getApplyid());
        ydPaypointShopInfoExt.setAgentid(ydShopApplyInfo.getAgentid());
        ydPaypointShopInfoExt.setBusinessCode(ydShopApplyInfo.getBusinessCode());
        ydPaypointShopInfoExt.setBusinessLicense(ydShopApplyInfo.getBusinessLicense());
        ydPaypointShopInfoExt.setBusinessLicenseImg(ydShopApplyInfo.getBusinessLicenseImg());
        ydPaypointShopInfoExt.setOwnerIdentificationCard(ydShopApplyInfo.getOwnerIdentificationCard());
        ydPaypointShopInfoExt.setIdentificationCardImg1(ydShopApplyInfo.getIdentificationCardImg1());
        ydPaypointShopInfoExt.setIdentificationCardImg2(ydShopApplyInfo.getIdentificationCardImg2());
        ydPaypointShopInfoExt.setLongitude(ydShopApplyInfo.getLongitude());
        ydPaypointShopInfoExt.setLatitude(ydShopApplyInfo.getLatitude());

        if(ydPaypointShopInfoExtMapper.insert(ydPaypointShopInfoExt)<=0){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "创建店铺失败，原因是扩展表插入失败！");
        }

        /**
         * 设置此店铺的owner与管理员
         */

        YdPaypointCpuserInfo paypointCpuserInfo = ydPaypointCpuserInfoMapper.selectByMobile(ydPaypointShopInfo.getMobile());
        if(paypointCpuserInfo==null){
            throw new YdException(ErrorCodeConstants.YD10000.getErrorCode(), "创建店铺失败，原因是"+ydPaypointShopInfo.getMobile()+"的法人手机号没有在[引灯智能店铺]公众号中绑定！");
        }

        //在合作伙伴用户记录中增加他所属于的店铺及角色
        CpUserInfoDTO cpUserInfoDTO = doMapper.map(paypointCpuserInfo,CpUserInfoDTO.class);
        cpUserInfoDTO.addListFeature(Constant.CP_SHOPS_OWNER,ydPaypointShopInfo.getShopid());
        cpUserInfoDTO.addListFeature(Constant.CP_SHOPS_MANAGER,ydPaypointShopInfo.getShopid());
        cpUserInfoDTO.addListFeature(Constant.DEFAULT_SHOP,ydPaypointShopInfo.getShopid());
        paypointCpuserInfo.setFeature(cpUserInfoDTO.getFeature());

        if(ydPaypointCpuserInfoMapper.updateByPrimaryKeySelective(paypointCpuserInfo)<=0){
            throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "创建店铺失败，原因是更新员工角色管理数据失败!");
        }

        /**
         * 员工角色管理表新增
         */
        if(ydPaypointShopworkerMapper.selectByShopid(ydPaypointShopInfo.getShopid())==null) {
            YdPaypointShopworker shopworker = new YdPaypointShopworker();
            shopworker.setShopid(ydPaypointShopInfo.getShopid());
            shopworker.setOwnerMobile(ydPaypointShopInfo.getMobile());
            shopworker.setManagerMobile(ydPaypointShopInfo.getMobile());
            if (ydPaypointShopworkerMapper.insert(shopworker) <= 0) {
                throw new YdException(ErrorCodeConstants.FAIL.getErrorCode(), "创建店铺失败，原因是创建员工角色管理数据失败!");
            }
        }

        /**
         * 为代理商商家管理数量加1
         */
        YdAgentInfo ydAgentInfo = ydAgentInfoMapper.selectByAgentid(ydShopApplyInfo.getAgentid());
        if(ydAgentInfo==null){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "代商商不存在!");
        }
        YdAgentInfoDTO ydAgentInfoDTO = doMapper.map(ydAgentInfo,YdAgentInfoDTO.class);
        String cpNumStr = ydAgentInfoDTO.getFeature(Constant.CPNUM);
        Integer cpNum = 0;
        if(StringUtil.isNotEmpty(cpNumStr)){
            cpNum = Integer.valueOf(cpNumStr);
            cpNum = cpNum + 1;
        }

        ydAgentInfoDTO.addFeature(Constant.CPNUM,cpNum.toString());
        ydAgentInfo.setFeature(ydAgentInfoDTO.getFeature());
        if(ydAgentInfoMapper.updateByPrimaryKeySelective(ydAgentInfo)<=0){
            throw new YdException(ErrorCodeConstants.PARAM_ERROR.getErrorCode(), "更新商家计数失败，导致店铺创建失败!");
        }


        return result;
    }


}
