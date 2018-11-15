package com.yd.ydsp.common.fasttext.security.xss;

import org.w3c.css.sac.LexicalUnit;

import java.util.Map;
import java.util.regex.Pattern;

public interface CssProcessHandler {
    public String printCssText(LexicalUnit lu, Map<String, Pattern> commonRegex);
}
