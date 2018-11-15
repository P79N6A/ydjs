package com.yd.ydsp.common.weixin;

public class TextMessage extends ReturnBaseMsg {

    private String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
        this.setMsgType("text");
    }
}
