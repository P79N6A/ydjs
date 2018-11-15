package com.yd.ydsp.common.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by zengyixun on 17/5/28.
 */
public class EncryptionUtil {
    /**
     * Base64 encode
     * */
    public static String base64Encode(String data){
        return Base64.encodeBase64String(data.getBytes());
    }

    /**
     * Base64 decode
     * @throws UnsupportedEncodingException
     * */
    public static String base64Decode(String data) throws UnsupportedEncodingException {
        return new String(Base64.decodeBase64(data.getBytes()),"utf-8");
    }

    /**
     * md5
     * */
    public static String md5Hex(String data){
        return DigestUtils.md5Hex(data);
    }

    /**
     * sha1
     * */
    public static String sha1Hex(String data){
        return DigestUtils.sha1Hex(data);
    }

    /**
     * sha256
     * */
    public static String sha256Hex(String data){
        return DigestUtils.sha256Hex(data);
    }

    /**
     * 对密码进行加密
     * @param value
     * @return
     */
    public static String encryptPassword(String value){
        return base64Encode(sha256Hex(value));
    }

    /**
     * 根据原始密码与盐，进行加密后密码计算
     * @param passwd
     * @param original
     * @return
     */
    public static String getPassword(String passwd,String original){

        StringBuffer stringBuffer = new StringBuffer(passwd.trim());
        String newPd = stringBuffer.insert(3, original.trim()).toString();
        return encryptPassword(newPd);

    }

    public static String encrypteWeiXinData(String encryptedData,String iv,String sessionKey) throws Exception {
        java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
        byte[] result = AES.decrypt(decoder.decode(encryptedData), decoder.decode(sessionKey),
                AES.generateIV(decoder.decode(iv)));
        String resultStr = StringUtils.toEncodedString(result, Charset.forName("UTF-8"));
        return resultStr;

    }

}
