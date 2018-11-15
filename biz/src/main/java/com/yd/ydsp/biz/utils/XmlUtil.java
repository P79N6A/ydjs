package com.yd.ydsp.biz.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.yd.ydsp.biz.pay.model.WeiXinBizPayQrcodePequestDO;
import com.yd.ydsp.biz.pay.model.WeiXinOrderQueryPequestDO;
import com.yd.ydsp.biz.pay.model.WeiXinPayRequestDO;
import com.yd.ydsp.common.weixin.Article;
import com.yd.ydsp.common.weixin.MusicMessage;
import com.yd.ydsp.common.weixin.NewsMessage;
import com.yd.ydsp.common.weixin.TextMessage;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class XmlUtil {
    //定义一个私有的静态全局变量来保存该类的唯一实例

    private static XmlUtil weiXinMessageUtil;

    /// 构造函数必须是私有的

    /// 这样在外部便无法使用 new 来创建该类的实例

    private XmlUtil()

    {

    }

    /// 定义一个全局访问点

    /// 设置为静态方法

    /// 则在类的外部便无需实例化就可以调用该方法

    public static XmlUtil getInstance()

    {

        //这里可以保证只实例化一次

        //即在第一次调用时实例化

        //以后调用便不会再实例化

        if (weiXinMessageUtil == null)

        {

            weiXinMessageUtil = new XmlUtil();

        }

        return weiXinMessageUtil;

    }

    /**
     * 解析输入流（XML）
     *
     * @param inputStream HttpServletRequest request的request.getInputStream()
     * @return
     * @throws Exception
     */

    public Map<String, String> parseXml(InputStream inputStream) throws Exception {

        // 将解析结果存储在HashMap中  

        Map<String, String> map = new HashMap<String, String>();

        // 读取输入流

        SAXReader reader = new SAXReader();

        Document document = reader.read(inputStream);

        String requestXml = document.asXML();

        String subXml = requestXml.split(">")[0] + ">";

        requestXml = requestXml.substring(subXml.length());

        // 得到xml根元素

        Element root = document.getRootElement();

        // 得到根元素的全部子节点

        List<Element> elementList = root.elements();

        // 遍历全部子节点

        for (Element e : elementList) {

            map.put(e.getName(), e.getText());

        }

        map.put("requestXml", requestXml);

        return map;
    }

    public Object xml2Object(String xmlString,Class c) throws DocumentException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Object result = null;
        Object object = c.newInstance();
        Map<String,Object> mapResult = this.xml2map(xmlString);
        if(object instanceof Map){
            return mapResult;
        }

        Field[] fields = c.getDeclaredFields();
        for(Field f:fields){
            if(mapResult.containsKey(f.getName())) {
                BeanUtils.copyProperty(object, f.getName(), mapResult.get(f.getName()));
            }
        }

        return result;

    }


    public Map xml2map(String xmlString) throws DocumentException {
        Document doc = DocumentHelper.parseText(xmlString);
        Element rootElement = doc.getRootElement();
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> result = new HashMap<String, Object>();
        ele2map(map, rootElement);
        for(String key: map.keySet()){
            Map<String,Object> tempMap = (Map<String, Object>) map.get(key);
            result.put(key,tempMap.get(key));
        }
        // 到此xml2map完成，下面的代码是将map转成了json用来观察我们的xml2map转换的是否ok
//        String string = JSONObject.fromObject(map).toString();
        return result;
    }

    private void ele2map(Map map, Element ele) {
        // 获得当前节点的子节点
        List<Element> elements = ele.elements();
        if (elements.size() == 0) {
            // 没有子节点说明当前节点是叶子节点，直接取值即可
            map.put(ele.getName(), ele.getText());
        } else if (elements.size() == 1) {
            // 只有一个子节点说明不用考虑list的情况，直接继续递归即可
            Map<String, Object> tempMap = new HashMap<String, Object>();
            ele2map(tempMap, elements.get(0));
            map.put(ele.getName(), tempMap);
        } else {
            // 多个子节点的话就得考虑list的情况了，比如多个子节点有节点名称相同的
            // 构造一个map用来去重
            Map<String, Object> tempMap = new HashMap<String, Object>();
            for (Element element : elements) {
                tempMap.put(element.getName(), null);
            }
            Set<String> keySet = tempMap.keySet();
            for (String string : keySet) {
                Namespace namespace = elements.get(0).getNamespace();
                List<Element> elements2 = ele.elements(new QName(string,namespace));
                // 如果同名的数目大于1则表示要构建list
                if (elements2.size() > 1) {
                    List<Map> list = new ArrayList<Map>();
                    for (Element element : elements2) {
                        Map<String, Object> tempMap1 = new HashMap<String, Object>();
                        ele2map(tempMap1, element);
                        list.add(tempMap1);
                    }
                    map.put(string, list);
                } else {
                    // 同名的数量不大于1则直接递归去
                    Map<String, Object> tempMap1 = new HashMap<String, Object>();
                    ele2map(tempMap1, elements2.get(0));
                    map.put(string, tempMap1);
                }
            }
        }
    }

    /**
     * 将请求的数据转化xml
     *
     * @param is     HttpServletRequest的request.getInputStream()
     * @return
     */

    public String parseMsgXml(InputStream is) {

        String responseMsg = null;

        try {

            int size = is.available();

            byte[] jsonBytes = new byte[size];

            is.read(jsonBytes);

            responseMsg = new String(jsonBytes, "UTF-8");


        } catch (Exception e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }

        return responseMsg;

    }


    public String getWeixinPayRequestData(WeiXinPayRequestDO weiXinPayRequestDO) {

        xstream.alias("xml", weiXinPayRequestDO.getClass());

        return xstream.toXML(weiXinPayRequestDO);

    }

    public String getWeiXinBizPayQrcodePequestData(WeiXinBizPayQrcodePequestDO weiXinBizPayQrcodePequestDO){
        xstream.alias("xml", weiXinBizPayQrcodePequestDO.getClass());

        return xstream.toXML(weiXinBizPayQrcodePequestDO);
    }

    public String getWeiXinOrderQueryPequestData(WeiXinOrderQueryPequestDO weiXinOrderQueryPequestDO){
        xstream.alias("xml", weiXinOrderQueryPequestDO.getClass());

        return xstream.toXML(weiXinOrderQueryPequestDO);
    }

    /**
     * 扩展xstream，使其支持CDATA块
     */

    private XStream xstream = new XStream(new XppDriver(new NoNameCoder()) {

        public HierarchicalStreamWriter createWriter(Writer out) {

            return new PrettyPrintWriter(out) {

                // 对全部xml节点的转换都添加CDATA标记  

                boolean cdata = true;

                @Override
                @SuppressWarnings("rawtypes")
                public void startNode(String name, Class clazz) {
                    super.startNode(name, clazz);
                    if (name.equals("detail")) {

                        cdata = false;

                    }
                }

                @Override
                public String encodeNode(String name) {
                    return name;
                }


                @Override
                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    } else {
                        writer.write(text);
                    }
                }

            };

        }

    });
}