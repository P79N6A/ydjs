package com.yd.ydsp.common.lang.enumeration;

/**
 * Created by zengyixun on 17/3/18.
 */

import com.yd.ydsp.common.lang.enumeration.Enum;
import com.yd.ydsp.common.lang.enumeration.Enum.EnumType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

public class EnumUtil {
    private static final Map entries = new WeakHashMap();

    public EnumUtil() {
    }

    public static Class getUnderlyingClass(Class enumClass) {
        return getEnumType(enumClass).getUnderlyingClass();
    }

    public static boolean isNameDefined(Class enumClass, String name) {
        return getEnumType(enumClass).nameMap.containsKey(name);
    }

    public static boolean isValueDefined(Class enumClass, Number value) {
        return getEnumType(enumClass).valueMap.containsKey(value);
    }

    public static Enum getEnumByName(Class enumClass, String name) {
        EnumType enumType = getEnumType(enumClass);
        if(enumType.enumList.size() != enumType.nameMap.size()) {
            enumType.populateNames(enumClass);
        }

        return (Enum)enumType.nameMap.get(name);
    }

    public static Enum getEnumByValue(Class enumClass, Number value) {
        return (Enum)getEnumType(enumClass).valueMap.get(value);
    }

    public static Enum getEnumByValue(Class enumClass, int value) {
        return (Enum)getEnumType(enumClass).valueMap.get(new Integer(value));
    }

    public static Enum getEnumByValue(Class enumClass, long value) {
        return (Enum)getEnumType(enumClass).valueMap.get(new Long(value));
    }

    public static Map getEnumMap(Class enumClass) {
        return Collections.unmodifiableMap(getEnumType(enumClass).nameMap);
    }

    public static Iterator getEnumIterator(Class enumClass) {
        return getEnumType(enumClass).enumList.iterator();
    }

    static Map getEnumEntryMap(Class enumClass) {
        ClassLoader classLoader = enumClass.getClassLoader();
        Object entryMap = null;
        Map var3 = entries;
        synchronized(entries) {
            entryMap = (Map)entries.get(classLoader);
            if(entryMap == null) {
                entryMap = new Hashtable();
                entries.put(classLoader, entryMap);
            }

            return (Map)entryMap;
        }
    }

    static EnumType getEnumType(Class enumClass) {
        if(enumClass == null) {
            throw new NullPointerException("The Enum class must not be null");
        } else {
            synchronized(enumClass) {
                if(!Enum.class.isAssignableFrom(enumClass)) {
                    throw new IllegalArgumentException(MessageFormat.format("Class \"{0}\" is not a subclass of Enum", new Object[]{enumClass.getName()}));
                } else {
                    Map entryMap = getEnumEntryMap(enumClass);
                    EnumType enumType = (EnumType)entryMap.get(enumClass.getName());
                    if(enumType == null) {
                        Method createEnumTypeMethod = findStaticMethod(enumClass, "createEnumType", new Class[0]);
                        if(createEnumTypeMethod != null) {
                            try {
                                enumType = (EnumType)createEnumTypeMethod.invoke((Object)null, new Object[0]);
                            } catch (IllegalAccessException var7) {
                                ;
                            } catch (IllegalArgumentException var8) {
                                ;
                            } catch (InvocationTargetException var9) {
                                ;
                            } catch (ClassCastException var10) {
                                ;
                            }
                        }

                        if(enumType != null) {
                            entryMap.put(enumClass.getName(), enumType);
                            enumType.populateNames(enumClass);
                        }
                    }

                    if(enumType == null) {
                        throw new UnsupportedOperationException(MessageFormat.format("Could not create EnumType for class \"{0}\"", new Object[]{enumClass.getName()}));
                    } else {
                        return enumType;
                    }
                }
            }
        }
    }

    private static Method findStaticMethod(Class enumClass, String methodName, Class[] paramTypes) {
        Method method = null;
        Class clazz = enumClass;

        while(!clazz.equals(Enum.class)) {
            try {
                method = clazz.getDeclaredMethod(methodName, paramTypes);
                break;
            } catch (NoSuchMethodException var6) {
                clazz = clazz.getSuperclass();
            }
        }

        return method != null && Modifier.isStatic(method.getModifiers())?method:null;
    }
}
