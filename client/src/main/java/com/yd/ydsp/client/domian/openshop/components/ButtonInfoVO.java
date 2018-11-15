package com.yd.ydsp.client.domian.openshop.components;

import java.io.Serializable;

public class ButtonInfoVO extends ResourceInfoVO implements Serializable {

    /**
     * 背景色
     */
    String backColor;
    /**
     * 字体色
     */
    String fontColor;
    /**
     * 字体大小
     */
    String fontSize;

    ButtonInfoVO(){
        this.resourceType = 3;
    }

    public String getBackColor() {
        return backColor;
    }

    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }
}
