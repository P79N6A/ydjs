package com.yd.ydsp.common.lang;

/**
 * Created by zengyixun on 17/3/18.
 */

import java.math.BigInteger;

public class MathUtil {
    public MathUtil() {
    }

    public static double getE() {
        return 2.718281828459045D;
    }

    public static double getPI() {
        return 3.141592653589793D;
    }

    public static double sin(double a) {
        return Math.sin(a);
    }

    public static double cos(double a) {
        return Math.cos(a);
    }

    public static double tan(double a) {
        return Math.tan(a);
    }

    public static double asin(double a) {
        return Math.asin(a);
    }

    public static double acos(double a) {
        return Math.acos(a);
    }

    public static double atan(double a) {
        return Math.atan(a);
    }

    public static double toRadians(double angdeg) {
        return Math.toRadians(angdeg);
    }

    public static double toDegrees(double angrad) {
        return Math.toDegrees(angrad);
    }

    public static double exp(double a) {
        return Math.exp(a);
    }

    public static double log(double a) {
        return Math.log(a);
    }

    public static double sqrt(double a) {
        return Math.sqrt(a);
    }

    public static double IEEEremainder(double f1, double f2) {
        return Math.IEEEremainder(f1, f2);
    }

    public static double ceil(double a) {
        return Math.ceil(a);
    }

    public static double floor(double a) {
        return Math.floor(a);
    }

    public static double rint(double a) {
        return Math.rint(a);
    }

    public static double atan2(double y, double x) {
        return Math.atan2(y, x);
    }

    public static double pow(double a, double b) {
        return Math.pow(a, b);
    }

    public static int round(float a) {
        return Math.round(a);
    }

    public static long round(double a) {
        return Math.round(a);
    }

    public static double random() {
        return Math.random();
    }

    public static int abs(int a) {
        return Math.abs(a);
    }

    public static long abs(long a) {
        return Math.abs(a);
    }

    public static float abs(float a) {
        return Math.abs(a);
    }

    public static double abs(double a) {
        return Math.abs(a);
    }

    public static int max(int a, int b) {
        return Math.max(a, b);
    }

    public static long max(long a, long b) {
        return Math.max(a, b);
    }

    public static float max(float a, float b) {
        return Math.max(a, b);
    }

    public static double max(double a, double b) {
        return Math.max(a, b);
    }

    public static int min(int a, int b) {
        return Math.min(a, b);
    }

    public static long min(long a, long b) {
        return Math.min(a, b);
    }

    public static float min(float a, float b) {
        return Math.min(a, b);
    }

    public static double min(double a, double b) {
        return Math.min(a, b);
    }

    public static boolean isPowerOfTwo(int intValue) {
        if(intValue == 0) {
            return false;
        } else {
            while((intValue & 1) == 0) {
                intValue >>>= 1;
            }

            return intValue == 1;
        }
    }

    public static boolean isPowerOfTwo(long longValue) {
        if(longValue == 0L) {
            return false;
        } else {
            while((longValue & 1L) == 0L) {
                longValue >>>= 1;
            }

            return longValue == 1L;
        }
    }

    public static boolean isPowerOfTwo(BigInteger bintValue) {
        int bitIndex = bintValue.getLowestSetBit();
        return bitIndex < 0?false:bintValue.clearBit(bitIndex).equals(BigInteger.ZERO);
    }
}
