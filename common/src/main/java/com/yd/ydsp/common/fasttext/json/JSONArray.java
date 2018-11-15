package com.yd.ydsp.common.fasttext.json;

import com.yd.ydsp.common.fasttext.json.JSONObject.EncodeType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author forrest.fengls
 */
public class JSONArray<T> {

    private List<T> jsonList = new ArrayList<T>();

    public String toString() {
        return toString(EncodeType.RAW);
    }

    /**
     * @param encodeType 编码类型
     * @return
     */
    public String toString(JSONObject.EncodeType encodeType) {
        Iterator<T> iter = jsonList.iterator();
        StringBuilder buffer = new StringBuilder();
        int i = 0;
        buffer.append('[');
        while (iter.hasNext()) {
            T value = iter.next();
            if (i++ > 0) {
                buffer.append(JSONObject.COMMA);
            }
            if (value instanceof String) {
                buffer.append(JSONObject.QUOTA);
                buffer.append(JSONObject.escape((String) value, encodeType));
                buffer.append(JSONObject.QUOTA);
            } else {
                ((JSONObject) value).build(encodeType, buffer);
            }
        }
        buffer.append(']');
        return buffer.toString();
    }

    /**
     * 加入元素，只能是JSONObject或者String类型
     * 
     * @param value
     * @return
     */
    public JSONArray<T> add(T value) {
        if (value instanceof JSONObject || value instanceof String) {
            this.jsonList.add(value);
        } else {
            throw new RuntimeException("class type must to be JSONObject or String");
        }
        return this;
    }

}
