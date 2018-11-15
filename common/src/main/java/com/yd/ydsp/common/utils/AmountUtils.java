package com.yd.ydsp.common.utils;

import java.math.BigDecimal;

/**
 * Created by zengyixun on 17/9/19.
 */
public class AmountUtils {

    /**
     * 将元转换为分
     * @param yuan
     * @return
     */
    public static int changeY2F(BigDecimal yuan){

        return yuan.multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP).toBigInteger().intValue();

    }

    /**
     * 将分转换为元
     * @param fen
     * @return
     */
    public static String changeF2Y(int fen){

        BigDecimal fenValue = new BigDecimal(fen);
        return fenValue.divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP).toString();

    }

    /**
     * 将一个BigDecimal转为保留两位小数的字串
     */

    public static String bigDecimal2Str(BigDecimal amount){
        return bigDecimal2Str(amount,2);

    }

    public static String bigDecimal2Str(BigDecimal amount,int scale){
        return amount.setScale(scale, BigDecimal.ROUND_HALF_UP).toString();

    }

    /**
     * 保留两位小数
     * @param amount
     * @return
     */
    public static BigDecimal bigDecimalBy2(BigDecimal amount){
        return amount.setScale(2, BigDecimal.ROUND_HALF_UP);

    }

    /**
     * 将分转换为元
     * @param fen
     * @return
     */
    public static BigDecimal changeF2YWithBigDecimal(int fen){

        BigDecimal fenValue = new BigDecimal(fen);
        return fenValue.divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP);

    }

    /**
     * 提供精确加法计算的add方法
     * @param value1 被加数
     * @param value2 加数
     * @return 两个参数的和
     */
    public static BigDecimal add(double value1,double value2){
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1).toString());
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2).toString());
        return b1.add(b2);
    }

    public static BigDecimal add(BigDecimal value1,BigDecimal value2){
        return value1.add(value2);
    }

    /**
     * 提供精确减法运算的sub方法
     * @param value1 减数
     * @param value2 减数
     * @return 两个参数的差
     */
    public static BigDecimal sub(double value1,double value2){
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1).toString());
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2).toString());
        return b1.subtract(b2);
    }

    /**
     * 提供精确减法运算的sub方法
     * @param value1 减数
     * @param value2 减数
     * @return 两个参数的差
     */
    public static BigDecimal sub(BigDecimal value1,BigDecimal value2){
        return value1.subtract(value2);
    }

    /**
     * 提供精确乘法运算的mul方法
     * @param value1 被乘数
     * @param value2 乘数
     * @return 两个参数的积
     */
    public static BigDecimal mul(double value1,double value2){
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1).toString());
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2).toString());
        return b1.multiply(b2);
    }

    /**
     * 提供精确乘法运算的mul方法
     * @param value1 被乘数
     * @param value2 乘数
     * @return 两个参数的积
     */
    public static BigDecimal mul(double value1,int value2){
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1).toString());
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2).toString());
        return b1.multiply(b2);
    }

    public static BigDecimal mul(BigDecimal value1,int value2){
        BigDecimal b1 = value1;
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2).toString());
        return b1.multiply(b2);
    }

    public static BigDecimal mul(BigDecimal value1,double value2){
        BigDecimal b1 = value1;
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2).toString());
        return b1.multiply(b2);
    }

    public static BigDecimal mul(BigDecimal value1,BigDecimal value2){
        BigDecimal b1 = value1;
        return b1.multiply(value2);
    }

    /**
     * 提供精确乘法运算的mul方法
     * @param value1 被乘数
     * @param value2 乘数
     * @return 两个参数的积
     */
    public static BigDecimal mul(int value1,int value2){
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1).toString());
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2).toString());
        return b1.multiply(b2);
    }

    /**
     * 提供精确的除法运算方法div
     * @param value1 被除数
     * @param value2 除数
     * @param scale 精确范围
     * @return 两个参数的商
     * @throws IllegalAccessException
     */
    public static BigDecimal div(double value1,double value2,int scale) throws IllegalAccessException{
        //如果精确范围小于0，抛出异常信息
        if(scale<0){
            throw new IllegalAccessException("精确度不能小于0");
        }
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1).toString());
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2).toString());
        return b1.divide(b2, scale);
    }

    public static BigDecimal div(int value1,int value2,int scale) throws IllegalAccessException {
        if(scale<0){
            throw new IllegalAccessException("精确度不能小于0");
        }
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1).toString());
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2).toString());
        return b1.divide(b2, scale);
    }


}
