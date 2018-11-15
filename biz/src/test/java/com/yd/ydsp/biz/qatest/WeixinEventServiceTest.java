package com.yd.ydsp.biz.qatest;

import com.yd.ydsp.biz.weixin.model.message.SubscribeEvent;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

/**
 * Created by zengyixun on 17/8/26.
 */
public class WeixinEventServiceTest extends BaseCase {

    @BeforeClass
    public void beforeClass() {
        System.out.println("this is before class");
    }

    @Test
    public void xmlToObectTest() throws JAXBException {
        String msg="<xml><ToUserName><![CDATA[gh_a1789d25d239]]></ToUserName> " +
                "<FromUserName><![CDATA[oX8yf1ZcOkt4eSji8eVuJ3ehq3Ow]]></FromUserName> " +
                "<CreateTime>1503721083</CreateTime> " +
                "<MsgType><![CDATA[event]]></MsgType> " +
                "<Event><![CDATA[subscribe]]></Event> " +
                "<EventKey><![CDATA[]]></EventKey> " +
                "</xml>";
        JAXBContext context = JAXBContext.newInstance(SubscribeEvent.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        SubscribeEvent subscribeEvent = (SubscribeEvent)unmarshaller.unmarshal(new StringReader(msg));

    }

    @AfterClass
    public void afterClass() {
        System.out.println("this is after class");
    }

}
