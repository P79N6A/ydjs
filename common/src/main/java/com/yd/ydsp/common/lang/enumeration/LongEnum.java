package com.yd.ydsp.common.lang.enumeration;

/**
 * Created by zengyixun on 17/3/18.
 */

public abstract class LongEnum extends Enum {
    private static final long serialVersionUID = 8152633183977823349L;

    public LongEnum() {
    }

    protected static final LongEnum create(long value) {
        return (LongEnum)createEnum(new Long(value));
    }

    protected static final LongEnum create(Number value) {
        return (LongEnum)createEnum(new Long(value.longValue()));
    }

    protected static final LongEnum create(String name, long value) {
        return (LongEnum)createEnum(name, new Long(value));
    }

    protected static final LongEnum create(String name, Number value) {
        return (LongEnum)createEnum(name, new Long(value.longValue()));
    }

    protected static Object createEnumType() {
        return new EnumType() {
            protected Class getUnderlyingClass() {
                return Long.class;
            }

            protected Number getNextValue(Number value, boolean flagMode) {
                if(value == null) {
                    return flagMode?new Long(1L):new Long(0L);
                } else {
                    long longValue = ((Long)value).longValue();
                    return flagMode?new Long(longValue << 1):new Long(longValue + 1L);
                }
            }

            protected boolean isZero(Number value) {
                return ((Long)value).longValue() == 0L;
            }
        };
    }

    public int intValue() {
        return ((Long)this.getValue()).intValue();
    }

    public long longValue() {
        return ((Long)this.getValue()).longValue();
    }

    public double doubleValue() {
        return ((Long)this.getValue()).doubleValue();
    }

    public float floatValue() {
        return ((Long)this.getValue()).floatValue();
    }

    public String toHexString() {
        return Long.toHexString((long)((Long)this.getValue()).intValue());
    }

    public String toOctalString() {
        return Long.toOctalString((long)((Long)this.getValue()).intValue());
    }

    public String toBinaryString() {
        return Long.toBinaryString((long)((Long)this.getValue()).intValue());
    }
}
