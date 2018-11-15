package com.yd.ydsp.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yd.ydsp.common.enums.paypoint.TypeEnum;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.weixin.mp.WXBizMsgCrypt;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Created by zengyixun on 17/8/27.
 */

public class RandomUtil {
    public static final String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LETTERCHAR = "abcdefghijkllmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMBERCHAR = "0123456789";
    public static final int[] NUMBERPARAM = { 1, 2, 3, 4, 5, 6, 7,8,9,0 };


    /**
     * 返回一个定长的随机字符串(只包含大小写字母、数字)
     *
     * @param length
     *            随机字符串长度
     * @return 随机字符串
     */
    public static String generateString(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
        }
        return sb.toString();
    }

    /**
     * 返回一个定长的随机纯字母字符串(只包含大小写字母)
     *
     * @param length
     *            随机字符串长度
     * @return 随机字符串
     */
    public static String generateMixString(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALLCHAR.charAt(random.nextInt(LETTERCHAR.length())));
        }
        return sb.toString();
    }

    /**
     * 返回一个定长的随机纯大写字母字符串(只包含大小写字母)
     *
     * @param length
     *            随机字符串长度
     * @return 随机字符串
     */
    public static String generateLowerString(int length) {
        return generateMixString(length).toLowerCase();
    }

    /**
     * 返回一个定长的随机纯小写字母字符串(只包含大小写字母)
     *
     * @param length
     *            随机字符串长度
     * @return 随机字符串
     */
    public static String generateUpperString(int length) {
        return generateMixString(length).toUpperCase();
    }

    /**
     * 生成一个定长的纯0字符串
     *
     * @param length
     *            字符串长度
     * @return 纯0字符串
     */
    public static String generateZeroString(int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append('0');
        }
        return sb.toString();
    }

    /**
     * 根据数字生成一个定长的字符串，长度不够前面补0
     *
     * @param num
     *            数字
     * @param fixdlenth
     *            字符串长度
     * @return 定长的字符串
     */
    public static String toFixdLengthString(long num, int fixdlenth) {
        StringBuffer sb = new StringBuffer();
        String strNum = String.valueOf(num);
        if (fixdlenth - strNum.length() >= 0) {
            sb.append(generateZeroString(fixdlenth - strNum.length()));
        } else {
            throw new RuntimeException("将数字" + num + "转化为长度为" + fixdlenth
                    + "的字符串发生异常！");
        }
        sb.append(strNum);
        return sb.toString();
    }

    /**
     * 每次生成的len位数都不相同
     *
     * @return 定长的数字
     */
    public static int getNotSimple(int len) {

        Random rand = new Random();
        for (int i = NUMBERPARAM.length; i > 1; i--) {
            int index = rand.nextInt(i);
            int tmp = NUMBERPARAM[index];
            NUMBERPARAM[index] = NUMBERPARAM[i - 1];
            NUMBERPARAM[i - 1] = tmp;
        }
        int result = 0;
        for (int i = 0; i < len; i++) {
            result = result * 10 + NUMBERPARAM[i];
        }
        return result;
    }

    /**
     * 用来生成各种18位id，比如订单id/店铺id等等
     * @param type
     * @return
     */
    public static String getSNCode(TypeEnum type){
        Instant.now().getEpochSecond();
        if(type==TypeEnum.CONSUMERORDER){
            return "29"+type.getName()+Instant.now().toEpochMilli()+getNotSimple(8);
        }else if(type==TypeEnum.CPORDER||type==TypeEnum.CPSUBORDER||
                type==TypeEnum.WAREITEM||type==TypeEnum.WARESKU){
            return "29"+type.getName()+Instant.now().toEpochMilli()+getNotSimple(6);
        }
        return "29"+type.getName()+Instant.now().getEpochSecond()+getNotSimple(4);
    }

    public static String getSignature(Map<String, String> params, boolean encode){

        List<String> keys = new ArrayList<String>(params.keySet());
        // Collections.sort(keys);//不按首字母排序, 需要按首字母排序请打开
        StringBuilder prestrSB = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (encode) {
                try {
                    value = URLEncoder.encode(value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                prestrSB.append(key).append("=").append(value);
            } else {
                prestrSB.append(key).append("=").append(value).append("&");
            }
        }
        String result =  prestrSB.toString()+"&key="+"780ca41041ef413ea378f7f06a08c9bc";
        result = result.toUpperCase();
        return EncryptionUtil.md5Hex(result).toUpperCase();

    }

    public static void main(String[] args) throws Exception {


//        Class stringClass = ShoppingOrderSkuVO.class;
//        Object o = stringClass.newInstance();
//        BeanUtils.copyProperty(o,"skuName","adfafadsfadsfasf");
//        BeanUtils.copyProperty(o,"count",8);
//
//        System.out.println(o);\\

        Long l1= 300600L;
        Integer i1 = 38;
        System.out.println(l1.longValue()>i1.intValue());

        String h = "http://sfasfasf.asfasf.asfasf.com/jasfaf.jpg";
        System.out.println(h.replaceFirst("http://","https://"));

        BigDecimal amount = new BigDecimal("-1.99");
        System.out.println(amount);

        Map<String,Object> map = new HashMap<>();
        map.put("af",1);
        List<Map> list = new ArrayList<>();
        list.add(map);
        System.out.println(Boolean.parseBoolean("0"));
        System.out.println(Boolean.parseBoolean("1"));
        System.out.println(StringUtil.StrIsJson(JSON.toJSONString(map)));
        System.out.println(StringUtil.StrIsJson(JSON.toJSONString(list)));

        LocalDate now = LocalDate.now();
        LocalDateTime time = LocalDateTime.now();
        System.out.println(now.getDayOfWeek().getValue());
        String dayStr = now.toString()+" 09:"+"08:"+"00";;

//        Integer second = DateUtils.getSecondTimestamp(DateUtils.dateStrToUdate(dayStr));
        Integer sec = DateUtils.getSecondTimestamp(new Date());
        System.out.println("time is :"+time.getSecond());
        System.out.println("time is :"+time.getMinute());
        System.out.println("time is :"+time.getHour());

        String domain = "www.ydjs360.com";
        Map<String,Object> requestBody = new HashMap<>();
        requestBody.put("action","add");
        Map<String,Object> setWebViewBody = new HashMap<>();
        setWebViewBody.put("action","add");
        if(StringUtil.isNotEmpty(domain)) {
            List<String> httpsList = new ArrayList<>();
            List<String> wssList = new ArrayList<>();
            httpsList.add("https://"+domain);
            wssList.add("wss://"+domain);
            requestBody.put("requestdomain",httpsList);
            requestBody.put("uploaddomain",httpsList);
            requestBody.put("downloaddomain",httpsList);
            requestBody.put("wsrequestdomain",wssList);
            setWebViewBody.put("webviewdomain",httpsList);
        }

        System.out.println(DateUtils.getToday());
        Date date = new Date();
        System.out.println(DateUtils.dateStrToUdate(DateUtils.date2StrWithYearAndMonthAndDay(date)));

        Integer money = AmountUtils.changeY2F(new BigDecimal("68"));

        Integer value = money / AmountUtils.changeY2F(new BigDecimal("68"));

        Integer point = value * 10;
        System.out.println(System.currentTimeMillis());
        System.out.println(point);

//        /**
//         * 写数据到excel
//         */
//
//        //第一步，创建一个workbook对应一个excel文件
//        HSSFWorkbook workbook = new HSSFWorkbook();
//        //第二部，在workbook中创建一个sheet对应excel中的sheet
//        HSSFSheet sheet = workbook.createSheet("用户表一");
//        //第三部，在sheet表中添加表头第0行，老版本的poi对sheet的行列有限制
//        HSSFRow row = sheet.createRow(0);
//        //第四步，创建单元格，设置表头
//        HSSFCell cell = row.createCell(0);
////        cell.setCellValue("用户名");
////        cell = row.createCell(1);
////        cell.setCellValue("密码");
//
//        String url="http://www.ydjs360.com/b2c/#/?qrcode=";
//        //第五步，写入实体数据，实际应用中这些数据从数据库得到,对象封装数据，集合包对象。对象的属性值对应表的每行的值
//        for (int qr = 0; qr < 1100; qr++) {
//            HSSFRow row1 = sheet.createRow(qr + 1);
//            //创建单元格设值
//            row1.createCell(0).setCellValue(qr+1);
//            row1.createCell(1).setCellValue(url.trim()+getSNCode(TypeEnum.QRCODE).trim());
//            Thread.sleep(1000);
//            System.out.println("已经完成"+qr+"条数据！");
//        }
//
//        //将文件保存到指定的位置
//        try {
//            FileOutputStream fos = new FileOutputStream("/Users/zengyixun/Documents/qrydjs.xls");
//            workbook.write(fos);
//            System.out.println("写入成功");
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();

//        }

        String data = "{\"item_list\":[{\"address\":\"pages/index/index\",\"tag\":\"果园\",\"first_class\": \"商家自营\",\"second_class\": \"鲜花/园艺/工品\",\"first_id\":304,\"second_id\":323,\"title\": \"华睿农业\"}]}";

              List<String> strings = new ArrayList<>();
        strings.add("adfaf");
        strings.add("哈哈");
        strings.add("哈哈");
        System.out.println(strings);
        Set<String> stringSet = new HashSet<>(strings);
        System.out.println(stringSet);


        String city= "298815163520184827";

        String subStr = city.substring(2,4);
        System.out.println(subStr);
        String city1= "重庆市";

        System.out.println(StringUtil.contains(city1,city));

//        /**达达测试
//         *
//         */
//        Map<String,String> mapBody = new HashMap<>();
//        mapBody.put("origin_shop_id","2524525262626");
//        String body = JSON.toJSONString(mapBody);
//        HashMap<String,Object> requestMap = new HashMap<>();
//        requestMap.put("app_key","dada3a230c631d2c7d2");
//        requestMap.put("body","");
//        requestMap.put("timestamp", DateUtils.getNowTimeStamp());
//        requestMap.put("format","json");
//        requestMap.put("v","1.0");
//        requestMap.put("source_id","73753");
//        //Key值排序
//        Collection<String> keySet = requestMap.keySet();
//        List<String> list = new ArrayList<String>(keySet);
//        Collections.sort(list);
//
//        //拼凑签名字符串
//        StringBuffer signStr = new StringBuffer();
//        for(int i=0; i<list.size(); i++){
//            String key = list.get(i);
//            signStr.append(key + requestMap.get(key));
//        }
//        //MD5签名
//        String mySign = EncryptionUtil.md5Hex("1b38815a90a45df4192781d5df076263" + signStr.toString() + "1b38815a90a45df4192781d5df076263");
//
//        String sign = mySign.toUpperCase();
//        requestMap.put("signature",sign);
//
//        String dadaurl = "http://newopen.qa.imdada.cn/api/cityCode/list";
//        String result = HttpUtil.postJson(dadaurl,JSON.toJSONString(requestMap));
//
//        System.out.println(result);
//
//        /**达达测试结束
//         *
//         */
        // 需要加密的明文
        String encodingAesKey = "aff671ea080522c9f7b216be89966a0b345lkjh5898";
        String token = "Ydjs360BYCXINQUCHANGYE16053079";
        String timestamp = "1528274665";
        String nonce = "1192383708";
        String appId = "wx4ba5fc60f616455a";
        String replyMsg = " 中文<xml><ToUserName><![CDATA[oia2TjjewbmiOUlr6X-1crbLOvLw]]></ToUserName><FromUserName><![CDATA[gh_7f083739789a]]></FromUserName><CreateTime>1407743423</CreateTime><MsgType><![CDATA[video]]></MsgType><Video><MediaId><![CDATA[eYJ1MbwPRJtOvIEabaxHs7TX2D-HV71s79GUxqdUkjm6Gs2Ed1KF3ulAOA9H1xG0]]></MediaId><Title><![CDATA[testCallBackReplyVideo]]></Title><Description><![CDATA[testCallBackReplyVideo]]></Description></Video></xml>";

        WXBizMsgCrypt pc = new WXBizMsgCrypt(token, encodingAesKey, appId);
        String mingwen = pc.encryptMsg(replyMsg, timestamp, nonce);
        System.out.println("加密后: " + mingwen);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        StringReader sr = new StringReader(mingwen);
        InputSource is = new InputSource(sr);
        Document document = db.parse(is);

        Element root = document.getDocumentElement();
        NodeList nodelist1 = root.getElementsByTagName("Encrypt");
        NodeList nodelist2 = root.getElementsByTagName("MsgSignature");

        String encrypt = nodelist1.item(0).getTextContent();
        String msgSignature = nodelist2.item(0).getTextContent();

        String format = "<xml><AppId><![CDATA[wx4ba5fc60f616455a]]></AppId><Encrypt><![CDATA[1zUJi14Ckbjf5IuB/afhMPebsU1+cXeEwNzn4D9x1TMibvSlyHE7OX0JhfmnGjqHbuPFAWiiCW11SaY3GPrah4sjQ0/Faq7Fkusw/xHYhRbMKbN+WEAl2zuyLf0SRNipbbZdeu8RH8EzknoQIW3FpdIvENM86mor8Nz+Aymig06KE30xNVWKmkOCFR/hFrF3vKYEKhHHkaCyQhJG+cmF3untPF8hSV3+x4y93zxAzshuUJGO5v9580RWOGYiEDD8dembAvqX9AnmQ6U0Pt5rXh6Cjy2/KvLe4djds4d8EjEQZi8W53BQlqGFph3so2SACr4w6sHArv1hFXzdUPOP5xosGC8s+WiiKOHDZN2foKxOD3rqPw1mE+xic9Rceeb6GEMNbGLsFyUNaFcKDYpYPHmndve9IBdKZK4eehfIgd9+yh5t4S9dmapdoHxbk7+TA4NY90fzvTR96gQK+IUbog==]]></Encrypt>" +
                "</xml>";
        String fromXML = String.format(format, encrypt);

        //
        // 公众平台发送消息给第三方，第三方处理
        //

        // 第三方收到公众号平台发送的消息
        String result2 = pc.decryptMsg("67227b55ef5b01d65bce36b8ac386f951f15a78c", timestamp, nonce, fromXML);
        System.out.println("解密后明文: " + result2);
        Map<String,Object> strMap = WeiXinMessageUtil.getInstance().xml2map(result2);
        String keyStr = ((HashMap<String,String>)strMap.get("ComponentVerifyTicket")).get("ComponentVerifyTicket");
        System.out.println(strMap);


    }
}
