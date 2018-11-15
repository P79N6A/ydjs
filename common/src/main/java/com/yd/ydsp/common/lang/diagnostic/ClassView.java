package com.yd.ydsp.common.lang.diagnostic;

/**
 * Created by zengyixun on 17/3/18.
 */

import com.yd.ydsp.common.lang.ClassLoaderUtil;
import com.yd.ydsp.common.lang.ClassUtil;
import com.yd.ydsp.common.lang.ObjectUtil;

public class ClassView {
    public ClassView() {
    }

    public static String toString(Class clazz) {
        return clazz.isPrimitive()?ClassUtil.getClassName(clazz):(clazz.isArray()?"Array " + ClassUtil.getClassName(clazz) + "\n" + toString(ClassUtil.getArrayComponentType(clazz)):(clazz.isInterface()?toInterfaceString(clazz, ""):toClassString(clazz, "")));
    }

    private static String toInterfaceString(Class clazz, String indent) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(indent).append("Interface ").append(clazz.getName()).append("  (").append(toClassString(clazz)).append(')');
        Class[] interfaceClass = clazz.getInterfaces();
        int i = 0;

        for(int c = interfaceClass.length; i < c; ++i) {
            clazz = interfaceClass[i];
            buffer.append('\n').append(toInterfaceString(clazz, indent + "  "));
        }

        return buffer.toString();
    }

    private static String toClassString(Class clazz, String indent) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(indent).append("Class ").append(clazz.getName()).append("  (").append(toClassString(clazz)).append(')');
        indent = indent + "  ";
        Class[] interfaceClass = clazz.getInterfaces();
        int i = 0;

        for(int c = interfaceClass.length; i < c; ++i) {
            buffer.append('\n').append(toInterfaceString(interfaceClass[i], indent));
        }

        clazz = clazz.getSuperclass();
        if(clazz != null) {
            buffer.append('\n').append(toClassString(clazz, indent));
        }

        return buffer.toString();
    }

    private static String toClassString(Class clz) {
        ClassLoader loader = clz.getClassLoader();
        return "loaded by " + ObjectUtil.identityToString(loader, "System ClassLoader") + ", " + ClassLoaderUtil.whichClass(clz.getName());
    }

    public static void main(String[] args) throws ClassNotFoundException {
        if(args.length == 0) {
            System.out.println("\nUsage:");
            System.out.println("    java " + ClassView.class.getName() + " MyClass");
            System.out.println("    java " + ClassView.class.getName() + " my.package.MyClass");
            System.out.println("    java " + ClassView.class.getName() + " META-INF/MANIFEST.MF");
            System.exit(-1);
        }

        for(int i = 0; i < args.length; ++i) {
            System.out.println(toString(ClassLoaderUtil.loadClass(args[i])));
        }

    }
}
