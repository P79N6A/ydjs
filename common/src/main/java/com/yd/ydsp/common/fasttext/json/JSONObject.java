package com.yd.ydsp.common.fasttext.json;

import com.yd.ydsp.common.fasttext.codec.HtmlFastEntities;
import com.yd.ydsp.common.fasttext.codec.JavaScriptEncode;

import java.util.HashMap;

/**
 * @author forrest.fengls
 */
public class JSONObject extends HashMap<String, Object> {

    private static final long serialVersionUID = -2013562412157561919L;

    public enum EncodeType {
        RAW,
        // js encode
        JAVASCRIPT_ENCODE,
        // html code
        HTML_ENCODE
    };

    /**
     * 默认分割符
     */
    protected static char COMMA = ',';
    protected static char QUOTA = '"';

    /**
     * 转换成json文本格式
     */
    public String toString() {
        return toString(EncodeType.RAW);
    }

    /**
     * 转换成json文本格式
     * 
     * @param encodeType: js encode or html encode
     * @return
     */

    public String toString(EncodeType encodeType) {
        StringBuilder buffer = new StringBuilder();
        build(encodeType, buffer);
        return buffer.toString();
    }

    /**
     * @param encodeType: js encode or html encode
     * @param buffer
     */
    void build(EncodeType encodeType, StringBuilder buffer) {
        int i = 0;
        buffer.append('{');
        for (String key : keySet()) {
            Object value = get(key);
            if (i++ > 0) {
                buffer.append(COMMA);
            }
            buildItem(key, value, encodeType, buffer);
        }
        buffer.append('}');
    }

    /**
     * @param key
     * @param value
     * @param encodeType
     * @param buffer
     */
    private void buildItem(String key, Object value, EncodeType encodeType, StringBuilder buffer) {

        buffer.append(QUOTA);
        buffer.append(escape(key, encodeType));
        buffer.append(QUOTA);
        buffer.append(':');
        if (value == null) {
            buffer.append("null");
            return;
        }

        if (value instanceof String) {
            buffer.append(QUOTA);
            buffer.append(escape((String) value, encodeType));
            buffer.append(QUOTA);
        } else {
            buffer.append(escape(value.toString(), encodeType));
        }
    }

    /**
     * 编码
     * 
     * @param string
     * @param encodeType: js encode or html encode
     * @return
     */
    protected static String escape(String string, EncodeType encodeType) {
        if (encodeType == EncodeType.JAVASCRIPT_ENCODE) {
            return JavaScriptEncode.escapedJavaScript(string);
        } else if (encodeType == EncodeType.HTML_ENCODE) {
            return HtmlFastEntities.HTML40.escape(string);
        } else {
            return string;
        }
    }

    /**
     * javascript encode
     * 
     * @param string 待被编码的字符串
     * @return
     */

}
