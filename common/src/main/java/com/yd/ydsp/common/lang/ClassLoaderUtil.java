package com.yd.ydsp.common.lang;

/**
 * Created by zengyixun on 17/3/18.
 */

import com.yd.ydsp.common.lang.ClassInstantiationException;
import com.yd.ydsp.common.lang.ClassUtil;
import com.yd.ydsp.common.lang.ServiceNotFoundException;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.lang.io.StreamUtil;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ClassLoaderUtil {
    public ClassLoaderUtil() {
    }

    public static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static Class loadClass(String className) throws ClassNotFoundException {
        return loadClass(className, (ClassLoader)getContextClassLoader());
    }

    public static Class loadClass(String className, Class referrer) throws ClassNotFoundException {
        ClassLoader classLoader = getReferrerClassLoader(referrer);
        return loadClass(className, (ClassLoader)classLoader);
    }

    public static Class loadClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
        return className == null?null:(classLoader == null?Class.forName(className):Class.forName(className, true, classLoader));
    }

    public static Class loadServiceClass(String serviceId) throws ClassNotFoundException {
        return loadServiceClass(serviceId, (ClassLoader)getContextClassLoader());
    }

    public static Class loadServiceClass(String serviceId, Class referrer) throws ClassNotFoundException {
        ClassLoader classLoader = getReferrerClassLoader(referrer);
        return loadServiceClass(serviceId, (ClassLoader)classLoader);
    }

    public static Class loadServiceClass(String serviceId, ClassLoader classLoader) throws ClassNotFoundException {
        if(serviceId == null) {
            return null;
        } else {
            serviceId = "META-INF/services/" + serviceId;
            InputStream istream = getResourceAsStream(serviceId, (ClassLoader)classLoader);
            if(istream == null) {
                throw new ServiceNotFoundException("Could not find " + serviceId);
            } else {
                String serviceClassName;
                try {
                    serviceClassName = StringUtil.trimToEmpty(StreamUtil.readText(istream, "UTF-8"));
                } catch (IOException var5) {
                    throw new ServiceNotFoundException("Failed to load " + serviceId, var5);
                }

                return loadClass(serviceClassName, (ClassLoader)classLoader);
            }
        }
    }

    public static Class loadServiceClass(String className, String serviceId) throws ClassNotFoundException {
        return loadServiceClass(className, serviceId, (ClassLoader)getContextClassLoader());
    }

    public static Class loadServiceClass(String className, String serviceId, Class referrer) throws ClassNotFoundException {
        ClassLoader classLoader = getReferrerClassLoader(referrer);
        return loadServiceClass(className, serviceId, (ClassLoader)classLoader);
    }

    public static Class loadServiceClass(String className, String serviceId, ClassLoader classLoader) throws ClassNotFoundException {
        try {
            if(className != null) {
                return loadClass(className, (ClassLoader)classLoader);
            }
        } catch (ClassNotFoundException var4) {
            ;
        }

        return loadServiceClass(serviceId, (ClassLoader)classLoader);
    }

    private static ClassLoader getReferrerClassLoader(Class referrer) {
        ClassLoader classLoader = null;
        if(referrer != null) {
            classLoader = referrer.getClassLoader();
            if(classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
        }

        return classLoader;
    }

    public static Object newInstance(String className) throws ClassNotFoundException, ClassInstantiationException {
        return newInstance((Class)loadClass(className));
    }

    public static Object newInstance(String className, Class referrer) throws ClassNotFoundException, ClassInstantiationException {
        return newInstance((Class)loadClass(className, (Class)referrer));
    }

    public static Object newInstance(String className, ClassLoader classLoader) throws ClassNotFoundException, ClassInstantiationException {
        return newInstance((Class)loadClass(className, (ClassLoader)classLoader));
    }

    private static Object newInstance(Class clazz) throws ClassInstantiationException {
        if(clazz == null) {
            return null;
        } else {
            try {
                return clazz.newInstance();
            } catch (InstantiationException var2) {
                throw new ClassInstantiationException("Failed to instantiate class: " + clazz.getName(), var2);
            } catch (IllegalAccessException var3) {
                throw new ClassInstantiationException("Failed to instantiate class: " + clazz.getName(), var3);
            } catch (Exception var4) {
                throw new ClassInstantiationException("Failed to instantiate class: " + clazz.getName(), var4);
            }
        }
    }

    public static Object newServiceInstance(String serviceId) throws ClassNotFoundException, ClassInstantiationException {
        return newInstance((Class)loadServiceClass(serviceId));
    }

    public static Object newServiceInstance(String serviceId, Class referrer) throws ClassNotFoundException, ClassInstantiationException {
        return newInstance((Class)loadServiceClass(serviceId, (Class)referrer));
    }

    public static Object newServiceInstance(String serviceId, ClassLoader classLoader) throws ClassNotFoundException, ClassInstantiationException {
        return newInstance((Class)loadServiceClass(serviceId, (ClassLoader)classLoader));
    }

    public static Object newServiceInstance(String className, String serviceId) throws ClassNotFoundException, ClassInstantiationException {
        return newInstance((Class)loadServiceClass(className, (String)serviceId));
    }

    public static Object newServiceInstance(String className, String serviceId, Class referrer) throws ClassNotFoundException, ClassInstantiationException {
        return newInstance((Class)loadServiceClass(className, serviceId, (Class)referrer));
    }

    public static Object newServiceInstance(String className, String serviceId, ClassLoader classLoader) throws ClassNotFoundException, ClassInstantiationException {
        return newInstance((Class)loadServiceClass(className, serviceId, (ClassLoader)classLoader));
    }

    public static URL[] getResources(String resourceName) {
        LinkedList urls = new LinkedList();
        boolean found = false;
        found = getResources(urls, resourceName, getContextClassLoader(), false);
        if(!found) {
            getResources(urls, resourceName, ClassLoaderUtil.class.getClassLoader(), false);
        }

        if(!found) {
            getResources(urls, resourceName, (ClassLoader)null, true);
        }

        return getDistinctURLs(urls);
    }

    public static URL[] getResources(String resourceName, Class referrer) {
        ClassLoader classLoader = getReferrerClassLoader(referrer);
        LinkedList urls = new LinkedList();
        getResources(urls, resourceName, classLoader, classLoader == null);
        return getDistinctURLs(urls);
    }

    public static URL[] getResources(String resourceName, ClassLoader classLoader) {
        LinkedList urls = new LinkedList();
        getResources(urls, resourceName, classLoader, classLoader == null);
        return getDistinctURLs(urls);
    }

    private static boolean getResources(List urlSet, String resourceName, ClassLoader classLoader, boolean sysClassLoader) {
        if(resourceName == null) {
            return false;
        } else {
            Enumeration i = null;

            try {
                if(classLoader != null) {
                    i = classLoader.getResources(resourceName);
                } else if(sysClassLoader) {
                    i = ClassLoader.getSystemResources(resourceName);
                }
            } catch (IOException var6) {
                ;
            }

            if(i != null && i.hasMoreElements()) {
                while(i.hasMoreElements()) {
                    urlSet.add(i.nextElement());
                }

                return true;
            } else {
                return false;
            }
        }
    }

    private static URL[] getDistinctURLs(LinkedList urls) {
        if(urls != null && urls.size() != 0) {
            HashSet urlSet = new HashSet(urls.size());
            Iterator i = urls.iterator();

            while(i.hasNext()) {
                URL url = (URL)i.next();
                if(urlSet.contains(url)) {
                    i.remove();
                } else {
                    urlSet.add(url);
                }
            }

            return (URL[])((URL[])urls.toArray(new URL[urls.size()]));
        } else {
            return new URL[0];
        }
    }

    public static URL getResource(String resourceName) {
        if(resourceName == null) {
            return null;
        } else {
            ClassLoader classLoader = null;
            URL url = null;
            classLoader = getContextClassLoader();
            if(classLoader != null) {
                url = classLoader.getResource(resourceName);
                if(url != null) {
                    return url;
                }
            }

            classLoader = ClassLoaderUtil.class.getClassLoader();
            if(classLoader != null) {
                url = classLoader.getResource(resourceName);
                if(url != null) {
                    return url;
                }
            }

            return ClassLoader.getSystemResource(resourceName);
        }
    }

    public static URL getResource(String resourceName, Class referrer) {
        if(resourceName == null) {
            return null;
        } else {
            ClassLoader classLoader = getReferrerClassLoader(referrer);
            return classLoader == null?ClassLoaderUtil.class.getClassLoader().getResource(resourceName):classLoader.getResource(resourceName);
        }
    }

    public static URL getResource(String resourceName, ClassLoader classLoader) {
        return resourceName == null?null:(classLoader == null?ClassLoaderUtil.class.getClassLoader().getResource(resourceName):classLoader.getResource(resourceName));
    }

    public static InputStream getResourceAsStream(String resourceName) {
        URL url = getResource(resourceName);

        try {
            if(url != null) {
                return url.openStream();
            }
        } catch (IOException var3) {
            ;
        }

        return null;
    }

    public static InputStream getResourceAsStream(String resourceName, Class referrer) {
        URL url = getResource(resourceName, (Class)referrer);

        try {
            if(url != null) {
                return url.openStream();
            }
        } catch (IOException var4) {
            ;
        }

        return null;
    }

    public static InputStream getResourceAsStream(String resourceName, ClassLoader classLoader) {
        URL url = getResource(resourceName, (ClassLoader)classLoader);

        try {
            if(url != null) {
                return url.openStream();
            }
        } catch (IOException var4) {
            ;
        }

        return null;
    }

    public static URL[] whichClasses(String className) {
        return getResources(ClassUtil.getClassNameAsResource(className));
    }

    public static URL[] whichClasses(String className, Class referrer) {
        return getResources(ClassUtil.getClassNameAsResource(className), (Class)referrer);
    }

    public static URL[] whichClasses(String className, ClassLoader classLoader) {
        return getResources(ClassUtil.getClassNameAsResource(className), (ClassLoader)classLoader);
    }

    public static URL whichClass(String className) {
        return getResource(ClassUtil.getClassNameAsResource(className));
    }

    public static URL whichClass(String className, Class referrer) {
        return getResource(ClassUtil.getClassNameAsResource(className), (Class)referrer);
    }

    public static URL whichClass(String className, ClassLoader classLoader) {
        return getResource(ClassUtil.getClassNameAsResource(className), (ClassLoader)classLoader);
    }
}

