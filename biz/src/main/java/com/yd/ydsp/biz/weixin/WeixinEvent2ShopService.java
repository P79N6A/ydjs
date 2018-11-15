package com.yd.ydsp.biz.weixin;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by zengyixun on 17/8/25.
 */
public interface WeixinEvent2ShopService {
    String ReceiveEventHandle(String appid, String msg) throws ParserConfigurationException, JAXBException, UnsupportedEncodingException;
}
