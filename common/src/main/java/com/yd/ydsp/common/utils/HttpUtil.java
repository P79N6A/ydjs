package com.yd.ydsp.common.utils;

import com.yd.ydsp.common.lang.StringUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by zengyixun on 17/5/15.
 */
public class HttpUtil {
    public static String get(String url,String charset,Map<String,String> headers) {
        if(StringUtil.isEmpty(charset)){
            charset = "UTF-8";
        }
        String result = "";
        try {
            String finalCharset = charset;
            Request request = Request.Get(url);
            if(headers!=null){
                for (String key : headers.keySet()) {

                    request = request.addHeader(key,headers.get(key));

                }
            }
            result = request
                    .connectTimeout(2000)
                    .socketTimeout(2000)
                    .execute()
                    .handleResponse(
                        //防止中文乱码

                        new ResponseHandler<String>() {

                            @Override

                            public String handleResponse(

                                    final HttpResponse response) throws IOException {

                                return EntityUtils.toString(response.getEntity(), Charset.forName(finalCharset));

                            }

                        }

                    );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String get(String url,String charset) {
        return get(url,charset,null);
    }

    public static String get(String url) {
        return get(url,null,null);
    }


    public static String post(String url,Form form){
        return post(url,form,null,null);
    }

    public static String post(String url, Form form, String charset,Map<String,String> headers){

        if(StringUtil.isEmpty(charset)){
            charset = "UTF-8";
        }
        if(form==null){
            form = Form.form();
        }
        String result = "";
        Request request =Request
                .Post(url);
        if(headers!=null){
            for (String key : headers.keySet()) {

                request= request.addHeader(key,headers.get(key));

            }
        }
        try {
            String finalCharset = charset;
            result = request
                    .connectTimeout(2000)
                    .socketTimeout(2000)
                    .bodyForm(form.build(), Charset.forName(finalCharset))
                    .execute().returnContent().asString(Charset.forName(finalCharset));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }

    public static String postJson(String url, String body, String charset) throws Exception {
        String responseContent = null;
        if(StringUtil.isEmpty(charset)){
            charset = "UTF-8";
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json");
            StringEntity stringEntity = new StringEntity(body);
            stringEntity.setContentEncoding(charset);
            httpPost.setEntity(stringEntity);

            response = httpClient.execute(httpPost);
//        System.out.println(response.getStatusLine().getStatusCode() + "\n");
            HttpEntity entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, charset);

//        System.out.println(responseContent);
        }finally {
            if(response!=null){
                response.close();
            }
            if(httpClient!=null) {
                httpClient.close();
            }
        }


        return responseContent;
    }

    public static byte[] postJsonGetByte(String url, String body, String charset) throws Exception {
        if(StringUtil.isEmpty(charset)){
            charset = "UTF-8";
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        byte[] result = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json");
            StringEntity stringEntity = new StringEntity(body);
            stringEntity.setContentEncoding(charset);
            httpPost.setEntity(stringEntity);

            response = httpClient.execute(httpPost);
//        System.out.println(response.getStatusLine().getStatusCode() + "\n");
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toByteArray(entity);

//        System.out.println(responseContent);
        }finally {
            if(response!=null){
                response.close();
            }
            if(httpClient!=null) {
                httpClient.close();
            }
        }


        return result;
    }

    public static String postJson(String url, String body) throws Exception {
        return postJson(url,body,null);
    }

    public static byte[] postJsonGetByte(String url, String body) throws Exception {
        return postJsonGetByte(url,body,null);
    }

    public static void main(String[] args) {
        System.out.println(post("http://localhost:7976/test/scheduler/add",null,null,null));
    }
}
