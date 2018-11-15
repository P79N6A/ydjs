package com.yd.ydsp.common.fasttext.security.xss.model;

import java.util.List;
import java.util.regex.Pattern;

import com.yd.ydsp.common.fasttext.security.xss.AttributeProcessHandler;

public class Attribute {

    public String                                   name;
    public String                                   defaultValue;
    public List<Pattern>                            allowedRegExp;
    public RestrictAttribute                        restrictAttribute = RestrictAttribute.NONE;
    public Class<? extends AttributeProcessHandler> backHandler;

    //这个handler是放在regexp-list元素中，当表达式匹配失败后采取的行为
    @Override
    public String toString() {
        return name;
    }

}
