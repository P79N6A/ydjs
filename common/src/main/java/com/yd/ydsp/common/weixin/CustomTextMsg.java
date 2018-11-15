package com.yd.ydsp.common.weixin;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CustomTextMsg extends CustomBaseMsg {

    public Map<String,String> text;

    public void setText(Map<String, String> text) {
        this.text = text;
    }

    public Map<String, String> getText() {
        return text;
    }
}
