package com.yd.ydsp.biz.rtmp;

import java.util.Map;

public interface CamHiService {
    /**
     * 摄像头登录
     * @param devId 设备ID
     * @param devMac 设备mac
     * @param swver 设备系统版本号
     * @param devPassword 设备登录密码
     * @return
    {"result":{
    "swver":%d,			//软件版本号，如果版本号>设备当前使用的swver，就去wget升级。
    "swUpdateUrl":"%s",		//软件升级地址

    "synctime":"%04d-%02d-%02d %02d:%02d:%02d",	//同步服务器时间。
    "tokenID":"%s",			//设备合法，给出服务凭证。
    },"flag":%d}		//flag=1表示成功。"result"字段返回正确参数(Json对象）。flag=0表示失败，"result"字段返回字符型原因解释。

    设备每次上电就DevLogin，如果失败了，就周期性反复尝试直至成功。间隔为3分钟。
     */
    Map<String,Object> DevLogin(String devId,String devMac,Integer swver,String devPassword);

    /**
     *
     * @param devToken 设备登录成功后得到的token
     * @return
     * {"result": {"resetEn"：%d, 1:复位设备。用于远程复位设备。

    "swver":%d,			//软件版本号，如果版本号>设备当前使用的swver，就去wget升级。
    "swUpdateUrl":"%s",		//软件升级地址

    "devName"："%s",	//用于修改设备的呢称（OSD上显示的）。
    "inStreamId"	//输入流id，1:主流，0:辅流
    "pushurl"："rtmp://xxx",  //新的推流地址
    "devRtmpEn"：%d, 0:stop, 1:start forever, 2:start in duration
    "rtmp_StartTime":"hh:mm",	//如果是2的话，就要给出duration定义。
    "rtmp_EndTime":"19:10",
    },"flag":1}
    参数有变动则返回相应字段，如果该参数无变化，则不用填，比如不复位设备，就不用填"resetEn"字段。

    设备定时进行设备请求，间隔时间为3秒。平台可以检测设备的Request请求，如果一段时间（60秒）无该请求，则判断设备已离线。
     */
    Map<String,Object> DevRequest(String devToken);
}
