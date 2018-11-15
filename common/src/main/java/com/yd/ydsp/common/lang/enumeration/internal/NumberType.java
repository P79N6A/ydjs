package com.yd.ydsp.common.lang.enumeration.internal;

/**
 * Created by zengyixun on 17/3/18.
 */

public interface NumberType {
    int RADIX_HEX = 16;
    int RADIX_OCT = 8;
    int RADIX_BIN = 2;

    int intValue();

    long longValue();

    float floatValue();

    double doubleValue();

    byte byteValue();

    short shortValue();

    String toHexString();

    String toOctalString();

    String toBinaryString();
}
