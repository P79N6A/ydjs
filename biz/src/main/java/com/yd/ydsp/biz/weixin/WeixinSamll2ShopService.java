package com.yd.ydsp.biz.weixin;

import com.yd.ydsp.biz.sso.model.UserSession;
import com.yd.ydsp.biz.weixin.model.WeixinSmallUserInfo;
import com.yd.ydsp.biz.weixin.model.WeixinUserInfo;

import java.io.IOException;
import java.util.Map;

public interface WeixinSamll2ShopService {
    Map<String,Object> loginUserByWeixinSmall(String appid, String code) throws Exception;

    /**
     * 保存用户基本信息
     * @param appid
     * @param weixinUserInfo
     * @return
     */
    boolean saveUserInfo(String appid,WeixinSmallUserInfo weixinUserInfo) throws Exception;

    /**
     * 小程序的服务端session过期时，用此接口生成一个新的session
     * @param appid
     * @param openid
     * @return
     */
    UserSession getNewSession(String appid, String openid,String yid) throws IOException;

    /**
     * 为已经授权的小程序上传小程序代码
     * @param appid
     * @param templateid
     * @return
     */
    boolean uploadWeiXinSmallCode(String appid,Integer templateid,String version,String desc) throws Exception;

    /**
     * {
     "item_list": [
     {
     "address":"index",
     "tag":"学习 生活",
     "first_class": "文娱",
     "second_class": "资讯",
     "first_id":1,
     "second_id":2,
     "title": "首页"
     }
     {
     "address":"page/logs/logs",
     "tag":"学习 工作",
     "first_class": "教育",
     "second_class": "学历教育",
     "third_class": "高等",
     "first_id":3,
     "second_id":4,
     "third_id":5,
     "title": "日志"
     }
     ]
     }

     item_list	提交审核项的一个列表（至少填写1项，至多填写5项）
     address	小程序的页面，可通过“获取小程序的第三方提交代码的页面配置”接口获得
     tag	小程序的标签，多个标签用空格分隔，标签不能多于10个，标签长度不超过20
     first_class	一级类目名称，可通过“获取授权小程序帐号的可选类目”接口获得
     second_class	二级类目(同上)
     third_class	三级类目(同上)
     first_id	一级类目的ID，可通过“获取授权小程序帐号的可选类目”接口获得
     second_id	二级类目的ID(同上)
     third_id	三级类目的ID(同上)
     title	小程序页面的标题,标题长度不超过32


     * @param appid
     * @param postData
     * @return
     */
    Map<String,Object> submitWeiXinSmallCodeAudit(String appid,String postData) throws Exception;

    /**
     * 小程序审核撤回

     单个帐号每天审核撤回次数最多不超过1次，一个月不超过10次。
     * @param appid
     * @return
     * @throws Exception
     */
    Map<String,Object> undoCodeAudit(String appid) throws Exception;

    /**
     * 查询最新一次提交的审核状态
     * @param appid
     * @return 审核状态，其中0为审核成功，1为审核失败，2为审核中
     */
    Map<String,Object> getXinSmallCodeAuditResult(String appid) throws Exception;

    /**
     * 发布已通过审核的小程序
     * @param appid
     * @return
     */
    Map<String,Object> releaseWeiXinSmallCode(String appid) throws Exception;

    /**
     * 灰度发布
     * @param appid
     * @param percentage 百分比
     * @return
     */
    Map<String,Object> grayReleaseWeiXinSmallCode(String appid,Integer percentage) throws Exception;

    /**
     * 查询灰度发布详情
     * @param appid
     * @return
     */
    Map<String,Object> getGrayReleaseWeiXinSmallCode(String appid) throws Exception;

    /**
     * 取消灰度发布
     * @param appid
     * @return
     */
    Map<String,Object> cancelGrayReleaseWeiXinSmallCode(String appid) throws Exception;

    /**
     * 获取授权小程序帐号的可选类目
     * 该接口可获取已设置的二级类目及用于代码审核的可选三级类目
     * @param appid
     * @return
     * {
    "errcode":0,
    "errmsg": "ok",
    "category_list" : [
    {
    "first_class":"工具",
    "second_class":"备忘录"，
    "first_id":1,
    "second_id":2,
    }
    {
    "first_class":"教育",
    "second_class":"学历教育",
    "third_class":"高等"
    "first_id":3,
    "second_id":4,
    "third_id":5,
    }
    ]
    }

    category_list	可填选的类目列表
    first_class	一级类目名称
    second_class	二级类目名称
    third_class	三级类目名称
    first_id	一级类目的ID编号
    second_id	二级类目的ID编号
    third_id	三级类目的ID编号

     */
    Map<String,Object> getCategory(String appid) throws Exception;

    /**
     *获取小程序的第三方提交代码的页面配置
     * @param appid
     * @return
     * {
    "errcode":0,
    "errmsg":"ok",
    "page_list":[
    "index",
    "page\/list",
    "page\/detail"
    ]
    }
     */
    Map<String,Object> getPage(String appid) throws Exception;


    /**
     * 生成小程序码
     * @param scene
     * @param page
     * @return
     */
    byte[] getWxAcodeUnlimit(String shopid,String scene, String page) throws Exception;

    /**
     * 设置小程序服务器域名及业务域名
     * action	add添加, delete删除, set覆盖, get获取。当参数是get时不需要填webviewdomain字段。如果没有action字段参数，则默认将开放平台第三方登记的小程序业务域名全部添加到授权的小程序中
     * @param appid
     * @return
     */
    Map<String,Object> modifySmallDomain(String appid,String action,String domain) throws Exception;

    /**
     * 获取体验者列表
     * @param appid
     * @return
     * @throws Exception
     */
    Map<String,Object> getTesterList(String appid) throws Exception;

    /**
     * 增加一个体验者，传入微信号
     * @param appid
     * @param wechatid
     * @return
     * @throws Exception
     * -1	系统繁忙
    85001	微信号不存在或微信号设置为不可搜索
    85002	小程序绑定的体验者数量达到上限
    85003	微信号绑定的小程序体验者达到上限
    85004	微信号已经绑定
     */
    Map<String,Object> bindTester(String appid,String wechatid) throws Exception;

    /**
     * 解除一个体验者
     * @param appid
     * @param wechatid
     * @return
     * @throws Exception
     */
    Map<String,Object> unBindTester(String appid,String wechatid) throws Exception;

    /**
     * 获取草稿箱内的所有临时代码草稿
     * @return
     */
    Map<String,Object> getTemplateDraftList() throws Exception;

    /**
     * 获取代码模版库中的所有小程序代码模版
     * @return
     */
    Map<String,Object> getTemplateList() throws Exception;

    /**
     * 将草稿箱的草稿选为小程序代码模版
     * @param draftId
     * @return
     */
    Map<String,Object> addToTemplate(Integer draftId) throws Exception;


    /**
     * 删除指定小程序代码模版
     * @param templateId
     * @return
     */
    Map<String,Object> deleteTemplate(Integer templateId) throws Exception;


}
