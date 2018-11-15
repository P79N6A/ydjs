package com.yd.ydsp.common.lang;

/**
 * Created by zengyixun on 17/3/18.
 */

import com.yd.ydsp.common.lang.ArrayUtil;
import com.yd.ydsp.common.lang.StringUtil;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class ClassUtil {
    public static final char RESOURCE_SEPARATOR_CHAR = '/';
    public static final char PACKAGE_SEPARATOR_CHAR = '.';
    public static final String PACKAGE_SEPARATOR = String.valueOf('.');
    public static final char INNER_CLASS_SEPARATOR_CHAR = '$';
    public static final String INNER_CLASS_SEPARATOR = String.valueOf('$');
    private static Map TYPE_MAP = Collections.synchronizedMap(new WeakHashMap());

    public ClassUtil() {
    }

    public static String getClassNameForObject(Object object) {
        return object == null?null:getClassName(object.getClass().getName(), true);
    }

    public static String getClassName(Class clazz) {
        return clazz == null?null:getClassName(clazz.getName(), true);
    }

    public static String getClassName(String className) {
        return getClassName(className, true);
    }

    private static String getClassName(String className, boolean processInnerClass) {
        if(StringUtil.isEmpty(className)) {
            return className;
        } else {
            if(processInnerClass) {
                className = className.replace('$', '.');
            }

            int length = className.length();
            int dimension = 0;

            for(int componentTypeName = 0; componentTypeName < length && className.charAt(componentTypeName) == 91; ++dimension) {
                ++componentTypeName;
            }

            if(dimension == 0) {
                return className;
            } else if(length <= dimension) {
                return className;
            } else {
                StringBuffer var6 = new StringBuffer();
                switch(className.charAt(dimension)) {
                    case 'B':
                        var6.append("byte");
                        break;
                    case 'C':
                        var6.append("char");
                        break;
                    case 'D':
                        var6.append("double");
                        break;
                    case 'E':
                    case 'G':
                    case 'H':
                    case 'K':
                    case 'M':
                    case 'N':
                    case 'O':
                    case 'P':
                    case 'Q':
                    case 'R':
                    case 'T':
                    case 'U':
                    case 'V':
                    case 'W':
                    case 'X':
                    case 'Y':
                    default:
                        return className;
                    case 'F':
                        var6.append("float");
                        break;
                    case 'I':
                        var6.append("int");
                        break;
                    case 'J':
                        var6.append("long");
                        break;
                    case 'L':
                        if(className.charAt(length - 1) != 59 || length <= dimension + 2) {
                            return className;
                        }

                        var6.append(className.substring(dimension + 1, length - 1));
                        break;
                    case 'S':
                        var6.append("short");
                        break;
                    case 'Z':
                        var6.append("boolean");
                }

                for(int i = 0; i < dimension; ++i) {
                    var6.append("[]");
                }

                return var6.toString();
            }
        }
    }

    public static String getShortClassNameForObject(Object object) {
        return object == null?null:getShortClassName((String)object.getClass().getName());
    }

    public static String getShortClassName(Class clazz) {
        return clazz == null?null:getShortClassName((String)clazz.getName());
    }

    public static String getShortClassName(String className) {
        if(StringUtil.isEmpty(className)) {
            return className;
        } else {
            className = getClassName(className, false);
            char[] chars = className.toCharArray();
            int lastDot = 0;

            for(int i = 0; i < chars.length; ++i) {
                if(chars[i] == 46) {
                    lastDot = i + 1;
                } else if(chars[i] == 36) {
                    chars[i] = 46;
                }
            }

            return new String(chars, lastDot, chars.length - lastDot);
        }
    }

    public static String getPackageNameForObject(Object object) {
        return object == null?null:getPackageName((String)object.getClass().getName());
    }

    public static String getPackageName(Class clazz) {
        return clazz == null?null:getPackageName((String)clazz.getName());
    }

    public static String getPackageName(String className) {
        if(StringUtil.isEmpty(className)) {
            return null;
        } else {
            className = getClassName(className, false);
            int i = className.lastIndexOf(46);
            return i == -1?"":className.substring(0, i);
        }
    }

    public static String getClassNameForObjectAsResource(Object object) {
        return object == null?null:object.getClass().getName().replace('.', '/') + ".class";
    }

    public static String getClassNameAsResource(Class clazz) {
        return clazz == null?null:clazz.getName().replace('.', '/') + ".class";
    }

    public static String getClassNameAsResource(String className) {
        return className == null?null:className.replace('.', '/') + ".class";
    }

    public static String getPackageNameForObjectAsResource(Object object) {
        return object == null?null:getPackageNameForObject(object).replace('.', '/');
    }

    public static String getPackageNameAsResource(Class clazz) {
        return clazz == null?null:getPackageName((Class)clazz).replace('.', '/');
    }

    public static String getPackageNameAsResource(String className) {
        return className == null?null:getPackageName((String)className).replace('.', '/');
    }

    public static Class getArrayClass(Class componentType, int dimension) {
        return dimension <= 0?componentType:(componentType == null?null: Array.newInstance(componentType, new int[dimension]).getClass());
    }

    public static Class getArrayComponentType(Class type) {
        return type == null?null:getTypeInfo(type).getArrayComponentType();
    }

    public static int getArrayDimension(Class clazz) {
        return clazz == null?-1:getTypeInfo(clazz).getArrayDimension();
    }

    public static List getSuperclasses(Class clazz) {
        return clazz == null?null:getTypeInfo(clazz).getSuperclasses();
    }

    public static List getInterfaces(Class clazz) {
        return clazz == null?null:getTypeInfo(clazz).getInterfaces();
    }

    public static boolean isInnerClass(Class clazz) {
        return clazz == null?false:StringUtil.contains(clazz.getName(), '$');
    }

    public static boolean isAssignable(Class[] classes, Class[] fromClasses) {
        if(!ArrayUtil.isSameLength(fromClasses, classes)) {
            return false;
        } else {
            if(fromClasses == null) {
                fromClasses = ArrayUtil.EMPTY_CLASS_ARRAY;
            }

            if(classes == null) {
                classes = ArrayUtil.EMPTY_CLASS_ARRAY;
            }

            for(int i = 0; i < fromClasses.length; ++i) {
                if(!isAssignable((Class)classes[i], (Class)fromClasses[i])) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean isAssignable(Class clazz, Class fromClass) {
        if(clazz == null) {
            return false;
        } else if(fromClass == null) {
            return !clazz.isPrimitive();
        } else if(clazz.isAssignableFrom(fromClass)) {
            return true;
        } else {
            if(clazz.isPrimitive()) {
                if(Boolean.TYPE.equals(clazz)) {
                    return Boolean.class.equals(fromClass);
                }

                if(Byte.TYPE.equals(clazz)) {
                    return Byte.class.equals(fromClass);
                }

                if(Character.TYPE.equals(clazz)) {
                    return Character.class.equals(fromClass);
                }

                if(Short.TYPE.equals(clazz)) {
                    return Short.class.equals(fromClass) || Byte.TYPE.equals(fromClass) || Byte.class.equals(fromClass);
                }

                if(Integer.TYPE.equals(clazz)) {
                    return Integer.class.equals(fromClass) || Byte.TYPE.equals(fromClass) || Byte.class.equals(fromClass) || Short.TYPE.equals(fromClass) || Short.class.equals(fromClass) || Character.TYPE.equals(fromClass) || Character.class.equals(fromClass);
                }

                if(Long.TYPE.equals(clazz)) {
                    return Long.class.equals(fromClass) || Integer.TYPE.equals(fromClass) || Integer.class.equals(fromClass) || Byte.TYPE.equals(fromClass) || Byte.class.equals(fromClass) || Short.TYPE.equals(fromClass) || Short.class.equals(fromClass) || Character.TYPE.equals(fromClass) || Character.class.equals(fromClass);
                }

                if(Float.TYPE.equals(clazz)) {
                    return Float.class.equals(fromClass) || Long.TYPE.equals(fromClass) || Long.class.equals(fromClass) || Integer.TYPE.equals(fromClass) || Integer.class.equals(fromClass) || Byte.TYPE.equals(fromClass) || Byte.class.equals(fromClass) || Short.TYPE.equals(fromClass) || Short.class.equals(fromClass) || Character.TYPE.equals(fromClass) || Character.class.equals(fromClass);
                }

                if(Double.TYPE.equals(clazz)) {
                    return Double.class.equals(fromClass) || Float.TYPE.equals(fromClass) || Float.class.equals(fromClass) || Long.TYPE.equals(fromClass) || Long.class.equals(fromClass) || Integer.TYPE.equals(fromClass) || Integer.class.equals(fromClass) || Byte.TYPE.equals(fromClass) || Byte.class.equals(fromClass) || Short.TYPE.equals(fromClass) || Short.class.equals(fromClass) || Character.TYPE.equals(fromClass) || Character.class.equals(fromClass);
                }
            }

            return false;
        }
    }

    protected static ClassUtil.TypeInfo getTypeInfo(Class type) {
        if(type == null) {
            throw new IllegalArgumentException("Parameter clazz should not be null");
        } else {
            Map var2 = TYPE_MAP;
            synchronized(TYPE_MAP) {
                ClassUtil.TypeInfo classInfo = (ClassUtil.TypeInfo)TYPE_MAP.get(type);
                if(classInfo == null) {
                    classInfo = new ClassUtil.TypeInfo(type);
                    TYPE_MAP.put(type, classInfo);
                }

                return classInfo;
            }
        }
    }

    public static Class getPrimitiveType(Class clazz) {
        return clazz == null?null:(clazz.isPrimitive()?clazz:(clazz.equals(Long.class)?Long.TYPE:(clazz.equals(Integer.class)?Integer.TYPE:(clazz.equals(Short.class)?Short.TYPE:(clazz.equals(Byte.class)?Byte.TYPE:(clazz.equals(Double.class)?Double.TYPE:(clazz.equals(Float.class)?Float.TYPE:(clazz.equals(Boolean.class)?Boolean.TYPE:(clazz.equals(Character.class)?Character.TYPE:null)))))))));
    }

    public static Class getNonPrimitiveType(Class clazz) {
        return clazz == null?null:(!clazz.isPrimitive()?clazz:(clazz.equals(Long.TYPE)?Long.class:(clazz.equals(Integer.TYPE)?Integer.class:(clazz.equals(Short.TYPE)?Short.class:(clazz.equals(Byte.TYPE)?Byte.class:(clazz.equals(Double.TYPE)?Double.class:(clazz.equals(Float.TYPE)?Float.class:(clazz.equals(Boolean.TYPE)?Boolean.class:(clazz.equals(Character.TYPE)?Character.class:null)))))))));
    }

    protected static class TypeInfo {
        private Class type;
        private Class componentType;
        private int dimension;
        private List superclasses;
        private List interfaces;

        private TypeInfo(Class type) {
            this.superclasses = new ArrayList(2);
            this.interfaces = new ArrayList(2);
            this.type = type;
            Class componentType = null;
            if(type.isArray()) {
                componentType = type;

                do {
                    componentType = componentType.getComponentType();
                    ++this.dimension;
                } while(componentType.isArray());
            }

            this.componentType = componentType;
            Class i;
            Class componentInterface;
            if(this.dimension > 0) {
                componentType = this.getNonPrimitiveType(componentType);
                i = componentType.getSuperclass();
                if(i == null && !Object.class.equals(componentType)) {
                    i = Object.class;
                }

                if(i != null) {
                    componentInterface = ClassUtil.getArrayClass(i, this.dimension);
                    this.superclasses.add(componentInterface);
                    this.superclasses.addAll(ClassUtil.getTypeInfo(componentInterface).superclasses);
                } else {
                    for(int var8 = this.dimension - 1; var8 >= 0; --var8) {
                        this.superclasses.add(ClassUtil.getArrayClass(Object.class, var8));
                    }
                }
            } else {
                type = this.getNonPrimitiveType(type);
                i = type.getSuperclass();
                if(i != null) {
                    this.superclasses.add(i);
                    this.superclasses.addAll(ClassUtil.getTypeInfo(i).superclasses);
                }
            }

            if(this.dimension == 0) {
                Class[] var7 = type.getInterfaces();
                ArrayList var10 = new ArrayList();

                Class interfaceClass;
                for(int i1 = 0; i1 < var7.length; ++i1) {
                    interfaceClass = var7[i1];
                    var10.add(interfaceClass);
                    var10.addAll(ClassUtil.getTypeInfo(interfaceClass).interfaces);
                }

                Iterator var11 = this.superclasses.iterator();

                while(var11.hasNext()) {
                    interfaceClass = (Class)var11.next();
                    var10.addAll(ClassUtil.getTypeInfo(interfaceClass).interfaces);
                }

                var11 = var10.iterator();

                while(var11.hasNext()) {
                    interfaceClass = (Class)var11.next();
                    if(!this.interfaces.contains(interfaceClass)) {
                        this.interfaces.add(interfaceClass);
                    }
                }
            } else {
                Iterator var9 = ClassUtil.getTypeInfo(componentType).interfaces.iterator();

                while(var9.hasNext()) {
                    componentInterface = (Class)var9.next();
                    this.interfaces.add(ClassUtil.getArrayClass(componentInterface, this.dimension));
                }
            }

        }

        private Class getNonPrimitiveType(Class type) {
            if(type.isPrimitive()) {
                if(Integer.TYPE.equals(type)) {
                    type = Integer.class;
                } else if(Long.TYPE.equals(type)) {
                    type = Long.class;
                } else if(Short.TYPE.equals(type)) {
                    type = Short.class;
                } else if(Byte.TYPE.equals(type)) {
                    type = Byte.class;
                } else if(Float.TYPE.equals(type)) {
                    type = Float.class;
                } else if(Double.TYPE.equals(type)) {
                    type = Double.class;
                } else if(Boolean.TYPE.equals(type)) {
                    type = Boolean.class;
                } else if(Character.TYPE.equals(type)) {
                    type = Character.class;
                }
            }

            return type;
        }

        public Class getType() {
            return this.type;
        }

        public Class getArrayComponentType() {
            return this.componentType;
        }

        public int getArrayDimension() {
            return this.dimension;
        }

        public List getSuperclasses() {
            return Collections.unmodifiableList(this.superclasses);
        }

        public List getInterfaces() {
            return Collections.unmodifiableList(this.interfaces);
        }
    }
}
