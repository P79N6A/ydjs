package com.yd.ydsp.common.lang.enumeration;

/**
 * Created by zengyixun on 17/3/18.
 */

public abstract class IntegerEnum extends Enum {
    private static final long serialVersionUID = 343392921439669443L;

    public IntegerEnum() {
    }

    protected static final IntegerEnum create(int value) {
        return (IntegerEnum)createEnum(new Integer(value));
    }

    protected static final IntegerEnum create(Number value) {
        return (IntegerEnum)createEnum(new Integer(value.intValue()));
    }

    protected static final IntegerEnum create(String name, int value) {
        return (IntegerEnum)createEnum(name, new Integer(value));
    }

    protected static final IntegerEnum create(String name, Number value) {
        return (IntegerEnum)createEnum(name, new Integer(value.intValue()));
    }

    protected static Object createEnumType() {
        return new EnumType() {
            protected Class getUnderlyingClass() {
                return Integer.class;
            }

            protected Number getNextValue(Number value, boolean flagMode) {
                if(value == null) {
                    return flagMode?new Integer(1):new Integer(0);
                } else {
                    int intValue = ((Integer)value).intValue();
                    return flagMode?new Integer(intValue << 1):new Integer(intValue + 1);
                }
            }

            protected boolean isZero(Number value) {
                return ((Integer)value).intValue() == 0;
            }
        };
    }

    public int intValue() {
        return ((Integer)this.getValue()).intValue();
    }

    public long longValue() {
        return ((Integer)this.getValue()).longValue();
    }

    public double doubleValue() {
        return ((Integer)this.getValue()).doubleValue();
    }

    public float floatValue() {
        return ((Integer)this.getValue()).floatValue();
    }

    public String toHexString() {
        return Integer.toHexString(((Integer)this.getValue()).intValue());
    }

    public String toOctalString() {
        return Integer.toOctalString(((Integer)this.getValue()).intValue());
    }

    public String toBinaryString() {
        return Integer.toBinaryString(((Integer)this.getValue()).intValue());
    }
}
