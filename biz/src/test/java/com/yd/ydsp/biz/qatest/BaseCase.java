package com.yd.ydsp.biz.qatest;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zengyixun on 17/8/26.
 */
public class BaseCase {
    protected ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("./spring/service-beans-test.xml");
}
