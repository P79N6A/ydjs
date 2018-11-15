package com.yd.ydsp.common.lang.enumeration;

/**
 * Created by zengyixun on 17/3/18.
 */

import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class BigIntegerEnum extends Enum {
    static final long serialVersionUID = 3407019802348379119L;

    public BigIntegerEnum() {
    }

    protected static final BigIntegerEnum create(long value) {
        return (BigIntegerEnum)createEnum(new BigInteger(String.valueOf(value)));
    }

    protected static final BigIntegerEnum create(String name, long value) {
        return (BigIntegerEnum)createEnum(name, new BigInteger(String.valueOf(value)));
    }

    protected static final BigIntegerEnum create(String name, String value) {
        return (BigIntegerEnum)createEnum(name, new BigInteger(value));
    }

    protected static final BigIntegerEnum create(BigInteger value) {
        return (BigIntegerEnum)createEnum(value);
    }

    protected static final BigIntegerEnum create(String name, BigInteger value) {
        return (BigIntegerEnum)createEnum(name, value);
    }

    protected static final BigIntegerEnum create(BigDecimal value) {
        return (BigIntegerEnum)createEnum(value.toBigInteger());
    }

    protected static final BigIntegerEnum create(String name, BigDecimal value) {
        return (BigIntegerEnum)createEnum(name, value.toBigInteger());
    }

    protected static final BigIntegerEnum create(Number value) {
        return (BigIntegerEnum)createEnum(new BigInteger(String.valueOf(value)));
    }

    protected static final BigIntegerEnum create(String name, Number value) {
        return (BigIntegerEnum)createEnum(name, new BigInteger(String.valueOf(value)));
    }

    protected static Object createEnumType() {
        return new EnumType() {
            protected Class getUnderlyingClass() {
                return BigInteger.class;
            }

            protected Number getNextValue(Number value, boolean flagMode) {
                return value == null?(flagMode?BigInteger.ONE:BigInteger.ZERO):(flagMode?((BigInteger)value).shiftLeft(1):((BigInteger)value).add(BigInteger.ONE));
            }

            protected boolean isZero(Number value) {
                return ((BigInteger)value).equals(BigInteger.ZERO);
            }
        };
    }

    public int intValue() {
        return ((BigInteger)this.getValue()).intValue();
    }

    public long longValue() {
        return ((BigInteger)this.getValue()).longValue();
    }

    public double doubleValue() {
        return ((BigInteger)this.getValue()).doubleValue();
    }

    public float floatValue() {
        return ((BigInteger)this.getValue()).floatValue();
    }

    public String toHexString() {
        return ((BigInteger)this.getValue()).toString(16);
    }

    public String toOctalString() {
        return ((BigInteger)this.getValue()).toString(8);
    }

    public String toBinaryString() {
        return ((BigInteger)this.getValue()).toString(2);
    }
}
