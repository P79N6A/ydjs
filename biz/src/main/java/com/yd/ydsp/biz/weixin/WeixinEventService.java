package com.yd.ydsp.biz.weixin;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.UnsupportedEncodingException;

/**
 * Created by zengyixun on 17/8/25.
 */
public interface WeixinEventService {
    String ReceiveEventHandle(String msg) throws ParserConfigurationException, JAXBException, UnsupportedEncodingException;
    Boolean checkToken(String token);
}
