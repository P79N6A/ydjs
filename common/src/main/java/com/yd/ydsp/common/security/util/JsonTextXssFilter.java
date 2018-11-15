package com.yd.ydsp.common.security.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.*;
import com.yd.ydsp.common.lang.StringUtil;

import java.lang.reflect.Method;
import java.util.List;


/**
 * 描述：json转义函数
 *
 * @author <a href="mailto:achi.zc@taobao.com">achi.zc</a>
 */
public class JsonTextXssFilter {

    public enum EscapeType {
        // no escape
        RAW,
        // js encode
        JAVASCRIPT_Escape,
        // html code
        HTML_Escape
    };


    private static Class stringEscapeUtil;
    private static Method method;


    /**
     * VauleFilter函数，转义key、value中的value值
     */
    private static ValueFilter vf = new ValueFilter() {

        @SuppressWarnings({ "rawtypes", "unchecked" })
        public Object process(Object source, String name, Object value) {
            if (value instanceof String) {
                String v = (String) value;
                return escape(v);
            }
            else if (value instanceof List) {
                List list = (List) value;

                for (int i = 0; i < list.size(); i++) {
                    Object obj = list.get(i);

                    if (obj instanceof String) {
                        String str = (String) obj;
                        obj = escape(str);
                        list.set(i, obj);
                    }
                    //end of if
                }
                //end of for
                return list;
            }
            //end of if

            return value;
        }

        /**
         *
         * @param str
         * @return
         */
        private String escape(String str) {

            return SecurityUtil.escapeHtml(str);
        }
    };

    /**
     * VauleFilter函数，转义key、value中的value值
     */
    private static NameFilter  nf = new NameFilter() {

        public String process(Object source, String name, Object value) {

            return SecurityUtil.escapeHtml(name);
        }
    };

    /**
     *
     * @param
     * @return
     */
    public static String jsonTextFilter(String jsonText,EscapeType escapeType) {

        if (StringUtil.isBlank(jsonText)) {
            return jsonText;
        }

        //long start = 0 ;
        //long end = 0;

        //parse from jsonText to fastjson
        String rt=null;
        try{
            //start = System.nanoTime();
            Object obj = JSON.parse(jsonText);
            //end = System.nanoTime();
            //System.out.println("JSON.parse CostTime: "+(end-start)+"ns");
            SerializerFeature[] features = new SerializerFeature[6];
            features[5] = SerializerFeature.WriteMapNullValue;
            features[1] = SerializerFeature.QuoteFieldNames;
            features[2] = SerializerFeature.SkipTransientField;
            features[3] = SerializerFeature.WriteEnumUsingToString;
            features[4] = SerializerFeature.SortField;
            features[0] = SerializerFeature.WriteTabAsSpecial;

            SerializeWriter out = new SerializeWriter(features);
            //SerializeWriter out = new SerializeWriter();
            JSONSerializer serialzer = new JSONSerializer(out);

            //end = System.nanoTime();
            //System.out.println("new serialzer CostTime: "+(end-start)+"ns");

            //serialize filter function
            serialzer.getNameFilters().add(nf);
            serialzer.getValueFilters().add(vf);

            //end = System.nanoTime();
            //System.out.println("serialzer filter CostTime: "+(end-start)+"ns");

            serialzer.write(obj);

            //end = System.nanoTime();
            //System.out.println("serialzer.write CostTime: "+(end-start)+"ns");

            rt = out.toString();

            //end = System.nanoTime();
            //System.out.println("toString CostTime: "+(end-start)+"ns");
            out.close();
        }catch(Exception e){
            return null;
        }


        //jsencode or not
        if (escapeType == EscapeType.HTML_Escape){
            return rt;
        }
        else if((escapeType == EscapeType.JAVASCRIPT_Escape))
        {
            return SecurityUtil.jsEncode(rt);
        }
        else{
            return rt;
        }

    }

    public static String jsonTextFilter(String jsonText) {
        return jsonTextFilter(jsonText,EscapeType.HTML_Escape);
    }

    public static String jsonTextFilter(Object jsonObj,EscapeType escapeType) {
        return jsonTextFilter(jsonObj.toString(),escapeType);
    }

    public static String jsonTextFilter(Object jsonObj) {
        return jsonTextFilter(jsonObj.toString(),EscapeType.HTML_Escape);
    }


}
