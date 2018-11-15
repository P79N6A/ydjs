package com.yd.ydsp.common.fasttext.security.xss;

import java.util.Map;


public interface AttributeProcessHandler {
    public void printAttribute(StringBuilder writerBuffer,String name,String value, Map<String, String> params);
}
