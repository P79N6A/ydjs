package com.yd.ydsp.common.utils;

import com.yd.ydsp.common.lang.ArrayUtil;
import com.yd.ydsp.common.lang.StringUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * @author zengyixun
 * @date 17/11/15
 */
public class CollectionUtil {

    private static final Logger logger = LoggerFactory.getLogger(CollectionUtil.class);

    public CollectionUtil() {
    }

    public static final boolean isEmpty(Collection c) {
        return c == null || c.size() == 0;
    }

    public static final void print(Collection c) {
        if(!isEmpty(c)) {
            Iterator var2 = c.iterator();

            while(var2.hasNext()) {
                Object object = var2.next();
            }

        }
    }

    public static final String[] getIdsFromListToStringArray(List list) {
        if(list != null && list.size() != 0) {
            List<String> ids = new ArrayList();
            Iterator var3 = list.iterator();

            while(var3.hasNext()) {
                Object object = var3.next();

                try {
                    Object id = BeanUtils.getProperty(object, "id");
                    if(id != null) {
                        ids.add(String.valueOf(id));
                    }
                } catch (Exception var5) {
                    logger.error("[CollectionUtil.getIdsFromListToStringArray] cant get ID from " + var5);
                }
            }

            return (String[])ids.toArray(new String[ids.size()]);
        } else {
            return ArrayUtil.EMPTY_STRING_ARRAY;
        }
    }

    public static final Integer[] getIdsFromListToIntegerArray(List list) {
        if(list != null && list.size() != 0) {
            List<Integer> ids = new ArrayList();
            Iterator var3 = list.iterator();

            while(var3.hasNext()) {
                Object object = var3.next();

                try {
                    Object id = BeanUtils.getProperty(object, "id");
                    if(id != null) {
                        ids.add(Integer.valueOf(String.valueOf(id)));
                    }
                } catch (Exception var5) {
                    logger.info("[CollectionUtil.getIdsFromListToIntegerArray] cant get ID from " + object);
                }
            }

            return (Integer[])ids.toArray(new Integer[ids.size()]);
        } else {
            return ArrayUtil.EMPTY_INTEGER_OBJECT_ARRAY;
        }
    }

    public static final Long[] getIdsFromListToLongArray(List list) {
        if(list != null && list.size() != 0) {
            List<Integer> ids = new ArrayList();
            Iterator var3 = list.iterator();

            while(var3.hasNext()) {
                Object object = var3.next();

                try {
                    Object id = BeanUtils.getProperty(object, "id");
                    if(id != null) {
                        ids.add(Integer.valueOf(String.valueOf(id)));
                    }
                } catch (Exception var5) {
                    logger.info("[CollectionUtil.getIdsFromListToLongArray] cant get ID from " + object);
                }
            }

            return (Long[])ids.toArray(new Long[ids.size()]);
        } else {
            return ArrayUtil.EMPTY_LONG_OBJECT_ARRAY;
        }
    }

    public static final String[] toStringArray(List list) {
        return isEmpty((Collection)list)?new String[0]:(String[])list.toArray(new String[list.size()]);
    }

    public static final Integer[] toIntegerArray(List list) {
        return isEmpty((Collection)list)?new Integer[0]:(Integer[])list.toArray(new Integer[list.size()]);
    }

    public static final Long[] toLongArray(List list) {
        return isEmpty((Collection)list)?new Long[0]:(Long[])list.toArray(new Long[list.size()]);
    }

    public static final String[] toStringArray(Set set) {
        return isEmpty((Collection)set)?new String[0]:(String[])set.toArray(new String[set.size()]);
    }

    public static final Integer[] toIntegerArray(Set set) {
        return isEmpty((Collection)set)?new Integer[0]:(Integer[])set.toArray(new Integer[set.size()]);
    }

    public static final Long[] toLongArray(Set set) {
        return isEmpty((Collection)set)?new Long[0]:(Long[])set.toArray(new Long[set.size()]);
    }

    public static final String[] toStringArray(String str) {
        return StringUtil.isBlank(str)?new String[0]:new String[]{str};
    }

    public static final String[] toStringArray(int i) {
        return new String[]{String.valueOf(i)};
    }

    public static final String[] toStringArray(long i) {
        return new String[]{String.valueOf(i)};
    }

    public static final String[] toStringArray(int[] is) {
        if(is != null && is.length != 0) {
            int length = is.length;
            String[] strings = new String[length];

            for(int i = 0; i < length; ++i) {
                String s = String.valueOf(is[i]);
                strings[i] = s;
            }

            return strings;
        } else {
            return new String[0];
        }
    }

    public static final String[] toStringArray(long[] is) {
        if(is != null && is.length != 0) {
            int length = is.length;
            String[] strings = new String[length];

            for(int i = 0; i < length; ++i) {
                String s = String.valueOf(is[i]);
                strings[i] = s;
            }

            return strings;
        } else {
            return new String[0];
        }
    }

    public static final List chopList(List list) {
        if(list == null) {
            return null;
        } else {
            for(int i = 0; i < list.size(); ++i) {
                if(list.get(i) == null) {
                    list.remove(i);
                    --i;
                }
            }

            return list;
        }
    }

    public static boolean isEmpty(Collection... colls) {
        boolean result = false;
        Collection[] var5 = colls;
        int var4 = colls.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            Collection<?> coll = var5[var3];
            if(coll == null || coll.isEmpty()) {
                result = true;
                break;
            }
        }

        return result;
    }

    public static boolean isEmpty(Map... maps) {
        boolean result = false;
        Map[] var5 = maps;
        int var4 = maps.length;

        for(int var3 = 0; var3 < var4; ++var3) {
            Map<?, ?> map = var5[var3];
            if(map == null || map.isEmpty()) {
                result = true;
                break;
            }
        }

        return result;
    }

    public static boolean isNotEmpty(Collection... colls) {
        return !isEmpty(colls);
    }

    public static boolean isNotEmpty(Map... maps) {
        return !isEmpty(maps);
    }

    public static final boolean isNotEmpty(Collection<?> c) {
        return c != null && !c.isEmpty();
    }

    public static final void merge(List<? extends Object> toList, String toKeyProperty, String setProperty, List<? extends Object> fromList, String fromKeyProperty) {
        if(!isEmpty((Collection)toList) && !isEmpty((Collection)fromList)) {
            try {
                Map<String, Object> m = new HashMap();
                Iterator var7 = fromList.iterator();

                Object to;
                while(var7.hasNext()) {
                    to = var7.next();
                    m.put(BeanUtils.getProperty(to, fromKeyProperty), to);
                }

                var7 = toList.iterator();

                while(var7.hasNext()) {
                    to = var7.next();
                    String v = BeanUtils.getProperty(to, toKeyProperty);
                    Object from = m.get(v);
                    if(from != null) {
                        BeanUtils.setProperty(to, setProperty, from);
                    }
                }
            } catch (Exception var10) {
                logger.error("",var10);
            }
        }

    }

    public static final void merge(List<? extends Object> toList, String toKeyProperty, String setProperty, Map<? extends Object, ? extends Object> fromMap) {
        if(!isEmpty((Collection)toList) && !(fromMap==null || fromMap.isEmpty())) {
            Map<String, Object> tmpMap = new HashMap();
            Iterator var6 = fromMap.entrySet().iterator();

            while(var6.hasNext()) {
                Map.Entry<? extends Object, ? extends Object> e = (Map.Entry)var6.next();
                tmpMap.put(String.valueOf(e.getKey()), e.getValue());
            }

            var6 = toList.iterator();

            while(var6.hasNext()) {
                Object to = var6.next();

                try {
                    String v = BeanUtils.getProperty(to, toKeyProperty);
                    Object o = tmpMap.get(v);
                    if(o != null) {
                        BeanUtils.setProperty(to, setProperty, o);
                    }
                } catch (Exception var9) {
                    logger.error("", var9);
                }
            }
        }

    }

    public static final String list2String(List<? extends Object> source, String sourceKeyProperty) {
        return list2String(source, sourceKeyProperty, ",", false);
    }

    public static final String list2String(List<? extends Object> source, String sourceKeyProperty, boolean omitDuplicate) {
        return list2String(source, sourceKeyProperty, ",", omitDuplicate);
    }

    public static final String list2String(List<? extends Object> source, String sourceKeyProperty, String separator, boolean omitDuplicate) {
        return list2String(source, sourceKeyProperty, separator, omitDuplicate, -1);
    }

    public static final String list2String(List<? extends Object> source, String sourceKeyProperty, String separator, boolean omitDuplicate, int size) {
        if(separator == null) {
            separator = ",";
        }

        String s = null;
        if(!isEmpty((Collection)source)) {
            Collection<Object> c = getObjectCollection(source, sourceKeyProperty, omitDuplicate);
            StringBuilder sBuilder = new StringBuilder();
            if(c != null) {
                int count = 0;
                Iterator var10 = c.iterator();

                while(var10.hasNext()) {
                    Object value = var10.next();
                    if(size > 0 && count >= size) {
                        break;
                    }

                    ++count;
                    sBuilder.append(value).append(separator);
                }

                s = sBuilder.toString();
                if(s.endsWith(separator)) {
                    s = s.substring(0, s.length() - separator.length());
                }
            }
        }

        return s;
    }

    public static final Collection<Object> getObjectCollection(List<? extends Object> source, String sourceKeyProperty, boolean filterDuplicate) {
        Collection<Object> c = null;
        if(filterDuplicate) {
            c = new HashSet();
        } else {
            c = new ArrayList();
        }

        Iterator var5 = source.iterator();

        while(var5.hasNext()) {
            Object o = var5.next();

            try {
                ((Collection)c).add(BeanUtils.getProperty(o, sourceKeyProperty));
            } catch (Exception var7) {
                logger.error("", var7);
            }
        }

        return (Collection)c;
    }

    public static String collection2String(Collection<? extends Object> collection) {
        return collection2String(collection, ",");
    }

    public static String collection2String(Collection<? extends Object> collection, String separator) {
        if(separator == null) {
            separator = ",";
        }

        StringBuilder sBuilder = new StringBuilder();
        Iterator var4 = collection.iterator();

        while(var4.hasNext()) {
            Object o = var4.next();
            sBuilder.append(String.valueOf(o)).append(separator);
        }

        String ret = sBuilder.toString();
        if(ret.endsWith(separator)) {
            ret = ret.substring(0, ret.lastIndexOf(separator));
        }

        return ret;
    }

    public static final long[] list2longArray(List<? extends Object> source, String sourceKeyProperty) {
        return list2longArray(source, sourceKeyProperty, false);
    }

    public static final long[] list2longArray(List<? extends Object> source, String sourceKeyProperty, boolean filterDuplicate) {
        long[] array = (long[])null;
        if(!isEmpty((Collection)source)) {
            Collection<Object> c = getObjectCollection(source, sourceKeyProperty, filterDuplicate);
            if(c != null) {
                array = collection2longArray(c);
            }
        }

        return array;
    }

    public static final long[] collection2longArray(Collection<Object> collection) {
        long[] array = new long[collection.size()];
        int index = 0;
        Iterator var4 = collection.iterator();

        while(var4.hasNext()) {
            Object value = var4.next();

            try {
                array[index] = Long.parseLong(String.valueOf(value));
                ++index;
            } catch (Exception var6) {
                logger.error("", var6);
            }
        }

        return array;
    }

    public static Map<String, Object> beanToMap(Object obj,boolean setNull) {
        Map<String, Object> params = new TreeMap<String,Object>();
        try {
            PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
            PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj);
            if(setNull) {
                for (int i = 0; i < descriptors.length; i++) {
                    String name = descriptors[i].getName();
                    if (!"class".equals(name)) {
                        params.put(name, propertyUtilsBean.getNestedProperty(obj, name));
                    }
                }
            }else{
                for (int i = 0; i < descriptors.length; i++) {
                    String name = descriptors[i].getName();
                    if (!"class".equals(name)) {
                        if(propertyUtilsBean.getNestedProperty(obj, name)!=null) {
                            params.put(name, propertyUtilsBean.getNestedProperty(obj, name));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public static Map<String, Object> beanToMap(Object obj) {
        return CollectionUtil.beanToMap(obj,false);
    }

}
