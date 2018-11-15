package com.yd.ydsp.common.lang.diagnostic;

/**
 * Created by zengyixun on 17/3/18.
 */

import com.yd.ydsp.common.lang.ClassLoaderUtil;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.lang.SystemUtil;
import java.io.File;
import java.net.URL;

public class JWhich {
    private static final String[] CLASSPATH = SystemUtil.getJavaRuntimeInfo().getClassPathArray();

    public JWhich() {
    }

    public static void which(String classOrResourceName) {
        URL[] classURLs = ClassLoaderUtil.whichClasses(classOrResourceName);
        URL[] resourceURLs = ClassLoaderUtil.getResources(classOrResourceName);
        if(classURLs.length == 0 && resourceURLs.length == 0) {
            System.out.println("\nClass or resource \'" + classOrResourceName + "\' not found.");
        } else {
            int i;
            if(classURLs.length > 0) {
                System.out.println("\nClass \'" + classOrResourceName + "\' found in: \n");

                for(i = 0; i < classURLs.length; ++i) {
                    System.out.println(StringUtil.alignRight("(" + (i + 1) + ")", 6) + " " + classURLs[i]);
                }
            }

            if(resourceURLs.length > 0) {
                System.out.println("\nResource \'" + classOrResourceName + "\' found in: \n");

                for(i = 0; i < resourceURLs.length; ++i) {
                    System.out.println(StringUtil.alignRight("(" + (i + 1) + ")", 6) + " " + resourceURLs[i]);
                }
            }
        }

    }

    public static void printClasspath() {
        System.out.println("\nSystem Classpath:");

        for(int i = 0; i < CLASSPATH.length; ++i) {
            String path = CLASSPATH[i];
            File file = new File(path);
            String errmsg = null;
            if(!file.exists()) {
                errmsg = "classpath element does not exist.";
            } else if(!file.isDirectory() && !path.toLowerCase().endsWith(".jar") && !path.toLowerCase().endsWith(".zip")) {
                errmsg = "classpath element is not a directory, .jar file, or .zip file.";
            }

            if(errmsg == null) {
                System.out.println(StringUtil.alignRight("(" + (i + 1) + ")", 6) + " " + path);
            } else {
                System.out.println(StringUtil.alignRight("(" + (i + 1) + ")", 6) + " " + path);
                System.out.println("wrong! " + errmsg);
            }
        }

    }

    public static void main(String[] args) {
        if(args.length == 0) {
            System.out.println("\nUsage:");
            System.out.println("    java " + JWhich.class.getName() + " MyClass");
            System.out.println("    java " + JWhich.class.getName() + " my.package.MyClass");
            System.out.println("    java " + JWhich.class.getName() + " META-INF/MANIFEST.MF");
            System.exit(-1);
        }

        for(int i = 0; i < args.length; ++i) {
            which(args[i]);
        }

        printClasspath();
    }
}
