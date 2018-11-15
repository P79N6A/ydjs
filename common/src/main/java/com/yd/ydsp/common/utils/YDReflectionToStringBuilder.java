package com.yd.ydsp.common.utils;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

/**
 *
 * Created by zengyixun on 16/3/7.
 */
public class YDReflectionToStringBuilder extends ReflectionToStringBuilder {

    public YDReflectionToStringBuilder(Object object) {
        super(object);
    }

    public YDReflectionToStringBuilder(Object object, org.apache.commons.lang3.builder.ToStringStyle style) {
        super(object, style);
    }

    public YDReflectionToStringBuilder(Object object, org.apache.commons.lang3.builder.ToStringStyle style, StringBuffer buffer) {
        super(object, style, buffer);
    }

    public <T> YDReflectionToStringBuilder(T object, org.apache.commons.lang3.builder.ToStringStyle style, StringBuffer buffer, Class<? super T> reflectUpToClass, boolean outputTransients, boolean outputStatics) {
        super(object, style, buffer, reflectUpToClass, outputTransients, outputStatics);
    }

    public static String reflectionToString(final Object object) {
        return YDReflectionToStringBuilder.toString(object);
    }

    /**
     * <p>Uses <code>ReflectionToStringBuilder</code> to generate a
     * <code>toString</code> for the specified object.</p>
     *
     * @param object the Object to be output
     * @param style  the style of the <code>toString</code> to create, may be <code>null</code>
     * @return the String result
     * @see ReflectionToStringBuilder#toString(Object, ToStringStyle)
     */
    public static String reflectionToString(final Object object, final ToStringStyle style) {
        return YDReflectionToStringBuilder.toString(object, style);
    }

    /**
     * <p>Uses <code>ReflectionToStringBuilder</code> to generate a
     * <code>toString</code> for the specified object.</p>
     *
     * @param object           the Object to be output
     * @param style            the style of the <code>toString</code> to create, may be <code>null</code>
     * @param outputTransients whether to include transient fields
     * @return the String result
     * @see ReflectionToStringBuilder#toString(Object, ToStringStyle, boolean)
     */
    public static String reflectionToString(final Object object, final ToStringStyle style, final boolean outputTransients) {
        return YDReflectionToStringBuilder.toString(object, style, outputTransients, false, null);
    }

    /**
     * <p>Uses <code>ReflectionToStringBuilder</code> to generate a
     * <code>toString</code> for the specified object.</p>
     *
     * @param <T>              the type of the object
     * @param object           the Object to be output
     * @param style            the style of the <code>toString</code> to create, may be <code>null</code>
     * @param outputTransients whether to include transient fields
     * @param reflectUpToClass the superclass to reflect up to (inclusive), may be <code>null</code>
     * @return the String result
     * @see ReflectionToStringBuilder#toString(Object, ToStringStyle, boolean, boolean, Class)
     * @since 2.0
     */
    public static <T> String reflectionToString(
            final T object,
            final ToStringStyle style,
            final boolean outputTransients,
            final Class<? super T> reflectUpToClass) {
        return YDReflectionToStringBuilder.toString(object, style, outputTransients, false, reflectUpToClass);
    }


    /**
     * <p>
     * Builds a <code>toString</code> value using the default <code>ToStringStyle</code> through reflection.
     * </p>
     * <p/>
     * <p>
     * It uses <code>AccessibleObject.setAccessible</code> to gain access to private fields. This means that it will
     * throw a security exception if run under a security manager, if the permissions are not set up correctly. It is
     * also not as efficient as testing explicitly.
     * </p>
     * <p/>
     * <p>
     * Transient members will be not be included, as they are likely derived. Static fields will not be included.
     * Superclass fields will be appended.
     * </p>
     *
     * @param object the Object to be output
     * @return the String result
     * @throws IllegalArgumentException if the Object is <code>null</code>
     */
    public static String toString(final Object object) {
        return toString(object, null, false, false, null);
    }

    /**
     * <p>
     * Builds a <code>toString</code> value through reflection.
     * </p>
     * <p/>
     * <p>
     * It uses <code>AccessibleObject.setAccessible</code> to gain access to private fields. This means that it will
     * throw a security exception if run under a security manager, if the permissions are not set up correctly. It is
     * also not as efficient as testing explicitly.
     * </p>
     * <p/>
     * <p>
     * Transient members will be not be included, as they are likely derived. Static fields will not be included.
     * Superclass fields will be appended.
     * </p>
     * <p/>
     * <p>
     * If the style is <code>null</code>, the default <code>ToStringStyle</code> is used.
     * </p>
     *
     * @param object the Object to be output
     * @param style  the style of the <code>toString</code> to create, may be <code>null</code>
     * @return the String result
     * @throws IllegalArgumentException if the Object or <code>ToStringStyle</code> is <code>null</code>
     */
    public static String toString(final Object object, final ToStringStyle style) {
        return toString(object, style, false, false, null);
    }

    /**
     * <p>
     * Builds a <code>toString</code> value through reflection.
     * </p>
     * <p/>
     * <p>
     * It uses <code>AccessibleObject.setAccessible</code> to gain access to private fields. This means that it will
     * throw a security exception if run under a security manager, if the permissions are not set up correctly. It is
     * also not as efficient as testing explicitly.
     * </p>
     * <p/>
     * <p>
     * If the <code>outputTransients</code> is <code>true</code>, transient members will be output, otherwise they
     * are ignored, as they are likely derived fields, and not part of the value of the Object.
     * </p>
     * <p/>
     * <p>
     * Static fields will not be included. Superclass fields will be appended.
     * </p>
     * <p/>
     * <p>
     * If the style is <code>null</code>, the default <code>ToStringStyle</code> is used.
     * </p>
     *
     * @param object           the Object to be output
     * @param style            the style of the <code>toString</code> to create, may be <code>null</code>
     * @param outputTransients whether to include transient fields
     * @return the String result
     * @throws IllegalArgumentException if the Object is <code>null</code>
     */
    public static String toString(final Object object, final ToStringStyle style, final boolean outputTransients) {
        return toString(object, style, outputTransients, false, null);
    }

    /**
     * <p>
     * Builds a <code>toString</code> value through reflection.
     * </p>
     * <p/>
     * <p>
     * It uses <code>AccessibleObject.setAccessible</code> to gain access to private fields. This means that it will
     * throw a security exception if run under a security manager, if the permissions are not set up correctly. It is
     * also not as efficient as testing explicitly.
     * </p>
     * <p/>
     * <p>
     * If the <code>outputTransients</code> is <code>true</code>, transient fields will be output, otherwise they
     * are ignored, as they are likely derived fields, and not part of the value of the Object.
     * </p>
     * <p/>
     * <p>
     * If the <code>outputStatics</code> is <code>true</code>, static fields will be output, otherwise they are
     * ignored.
     * </p>
     * <p/>
     * <p>
     * Static fields will not be included. Superclass fields will be appended.
     * </p>
     * <p/>
     * <p>
     * If the style is <code>null</code>, the default <code>ToStringStyle</code> is used.
     * </p>
     *
     * @param object           the Object to be output
     * @param style            the style of the <code>toString</code> to create, may be <code>null</code>
     * @param outputTransients whether to include transient fields
     * @param outputStatics    whether to include transient fields
     * @return the String result
     * @throws IllegalArgumentException if the Object is <code>null</code>
     * @since 2.1
     */
    public static String toString(final Object object, final ToStringStyle style, final boolean outputTransients, final boolean outputStatics) {
        return toString(object, style, outputTransients, outputStatics, null);
    }

    /**
     * <p>
     * Builds a <code>toString</code> value through reflection.
     * </p>
     * <p/>
     * <p>
     * It uses <code>AccessibleObject.setAccessible</code> to gain access to private fields. This means that it will
     * throw a security exception if run under a security manager, if the permissions are not set up correctly. It is
     * also not as efficient as testing explicitly.
     * </p>
     * <p/>
     * <p>
     * If the <code>outputTransients</code> is <code>true</code>, transient fields will be output, otherwise they
     * are ignored, as they are likely derived fields, and not part of the value of the Object.
     * </p>
     * <p/>
     * <p>
     * If the <code>outputStatics</code> is <code>true</code>, static fields will be output, otherwise they are
     * ignored.
     * </p>
     * <p/>
     * <p>
     * Superclass fields will be appended up to and including the specified superclass. A null superclass is treated as
     * <code>java.lang.Object</code>.
     * </p>
     * <p/>
     * <p>
     * If the style is <code>null</code>, the default <code>ToStringStyle</code> is used.
     * </p>
     *
     * @param <T>              the type of the object
     * @param object           the Object to be output
     * @param style            the style of the <code>toString</code> to create, may be <code>null</code>
     * @param outputTransients whether to include transient fields
     * @param outputStatics    whether to include static fields
     * @param reflectUpToClass the superclass to reflect up to (inclusive), may be <code>null</code>
     * @return the String result
     * @throws IllegalArgumentException if the Object is <code>null</code>
     * @since 2.1
     */
    public static <T> String toString(
            final T object, final ToStringStyle style, final boolean outputTransients,
            final boolean outputStatics, final Class<? super T> reflectUpToClass) {
        return new YDReflectionToStringBuilder(object, style, null, reflectUpToClass, outputTransients, outputStatics)
                .toString();
    }


    @Override
    protected void appendFieldsIn(Class clazz) {
        if (clazz.isArray()) {
            this.reflectionAppendArray(this.getObject());
            return;
        }
        final Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        for (final Field field : fields) {
            final String fieldName = field.getName();
            if (this.accept(field)) {
                try {
                    // Warning: Field.get(Object) creates wrappers objects
                    // for primitive types.
                    final Object fieldValue = this.getValue(field);
                    if (fieldValue != null) {
                        this.append(fieldName, fieldValue);
                    }
                } catch (final IllegalAccessException ex) {
                    //this can't happen. Would get a Security exception
                    // instead
                    //throw a runtime exception in case the impossible
                    // happens.
                    throw new InternalError("Unexpected IllegalAccessException: " + ex.getMessage());
                }
            }
        }
    }
}
