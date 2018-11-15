package com.yd.ydsp.common.lang.i18n;

/**
 * Created by zengyixun on 17/3/18.
 */

import com.yd.ydsp.common.lang.StringUtil;

import java.nio.charset.Charset;
import java.util.*;

public class LocaleUtil {
    private static final Set AVAILABLE_LANGUAGES = new HashSet();
    private static final Set AVAILABLE_COUNTRIES = new HashSet();
    private static final LocaleInfo systemLocaleInfo;
    private static LocaleInfo defaultLocalInfo;
    private static final ThreadLocal contextLocaleInfoHolder = new ThreadLocal();

    public LocaleUtil() {
    }

    public static Locale parseLocale(String localeName) {
        if(localeName != null) {
            String[] localeParts = StringUtil.split(localeName, "_");
            int len = localeParts.length;
            if(len > 0) {
                String language = localeParts[0];
                String country = "";
                String variant = "";
                if(len > 1) {
                    country = localeParts[1];
                }

                if(len > 2) {
                    variant = localeParts[2];
                }

                return new Locale(language, country, variant);
            }
        }

        return null;
    }

    public static LocaleInfo parseLocaleInfo(String localeName) {
        Locale locale = null;
        String charset = null;
        if(StringUtil.isNotEmpty(localeName)) {
            int index = localeName.indexOf(":");
            String localePart = localeName;
            String charsetPart = null;
            if(index >= 0) {
                localePart = localeName.substring(0, index);
                charsetPart = localeName.substring(index + 1);
            }

            locale = parseLocale(localePart);
            charset = StringUtil.trimToNull(charsetPart);
        }

        return new LocaleInfo(locale, charset);
    }

    public static boolean isLocaleSupported(Locale locale) {
        return locale != null && AVAILABLE_LANGUAGES.contains(locale.getLanguage()) && AVAILABLE_COUNTRIES.contains(locale.getCountry());
    }

    public static boolean isCharsetSupported(String charset) {
        return Charset.isSupported(charset);
    }

    public static String getCanonicalCharset(String charset) {
        return Charset.forName(charset).name();
    }

    public static String getCanonicalCharset(String charset, String defaultCharset) {
        String result = null;

        try {
            result = getCanonicalCharset(charset);
        } catch (IllegalArgumentException var6) {
            if(defaultCharset != null) {
                try {
                    result = getCanonicalCharset(defaultCharset);
                } catch (IllegalArgumentException var5) {
                    ;
                }
            }
        }

        return result;
    }

    public static List calculateBundleNames(String baseName, Locale locale) {
        return calculateBundleNames(baseName, locale, false);
    }

    public static List calculateBundleNames(String baseName, Locale locale, boolean noext) {
        baseName = StringUtil.defaultIfEmpty(baseName);
        if(locale == null) {
            locale = new Locale("");
        }

        String ext = "";
        int extLength = 0;
        if(!noext) {
            int result = baseName.lastIndexOf(".");
            if(result != -1) {
                ext = baseName.substring(result, baseName.length());
                extLength = ext.length();
                baseName = baseName.substring(0, result);
                if(extLength == 1) {
                    ext = "";
                    extLength = 0;
                }
            }
        }

        ArrayList result1 = new ArrayList(4);
        String language = locale.getLanguage();
        int languageLength = language.length();
        String country = locale.getCountry();
        int countryLength = country.length();
        String variant = locale.getVariant();
        int variantLength = variant.length();
        StringBuffer buffer = new StringBuffer(baseName);
        buffer.append(ext);
        result1.add(buffer.toString());
        buffer.setLength(buffer.length() - extLength);
        if(languageLength + countryLength + variantLength == 0) {
            return result1;
        } else {
            if(buffer.length() > 0) {
                buffer.append('_');
            }

            buffer.append(language);
            if(languageLength > 0) {
                buffer.append(ext);
                result1.add(buffer.toString());
                buffer.setLength(buffer.length() - extLength);
            }

            if(countryLength + variantLength == 0) {
                return result1;
            } else {
                buffer.append('_').append(country);
                if(countryLength > 0) {
                    buffer.append(ext);
                    result1.add(buffer.toString());
                    buffer.setLength(buffer.length() - extLength);
                }

                if(variantLength == 0) {
                    return result1;
                } else {
                    buffer.append('_').append(variant);
                    buffer.append(ext);
                    result1.add(buffer.toString());
                    buffer.setLength(buffer.length() - extLength);
                    return result1;
                }
            }
        }
    }

    public static LocaleInfo getSystem() {
        return systemLocaleInfo;
    }

    public static LocaleInfo getDefault() {
        return defaultLocalInfo == null?systemLocaleInfo:defaultLocalInfo;
    }

    public static LocaleInfo setDefault(Locale locale) {
        LocaleInfo old = getDefault();
        defaultLocalInfo = new LocaleInfo(locale, (String)null, systemLocaleInfo);
        return old;
    }

    public static LocaleInfo setDefault(Locale locale, String charset) {
        LocaleInfo old = getDefault();
        defaultLocalInfo = new LocaleInfo(locale, charset, systemLocaleInfo);
        return old;
    }

    public static LocaleInfo setDefault(LocaleInfo localeInfo) {
        if(localeInfo == null) {
            return setDefault((Locale)null, (String)null);
        } else {
            LocaleInfo old = getDefault();
            defaultLocalInfo = localeInfo;
            return old;
        }
    }

    public static void resetDefault() {
        defaultLocalInfo = systemLocaleInfo;
    }

    public static LocaleInfo getContext() {
        LocaleInfo contextLocaleInfo = (LocaleInfo)contextLocaleInfoHolder.get();
        return contextLocaleInfo == null?getDefault():contextLocaleInfo;
    }

    public static LocaleInfo setContext(Locale locale) {
        LocaleInfo old = getContext();
        contextLocaleInfoHolder.set(new LocaleInfo(locale, (String)null, defaultLocalInfo));
        return old;
    }

    public static LocaleInfo setContext(Locale locale, String charset) {
        LocaleInfo old = getContext();
        contextLocaleInfoHolder.set(new LocaleInfo(locale, charset, defaultLocalInfo));
        return old;
    }

    public static LocaleInfo setContext(LocaleInfo localeInfo) {
        if(localeInfo == null) {
            return setContext((Locale)null, (String)null);
        } else {
            LocaleInfo old = getContext();
            contextLocaleInfoHolder.set(localeInfo);
            return old;
        }
    }

    public static void resetContext() {
        contextLocaleInfoHolder.set((Object)null);
    }

    static {
        Locale[] availableLocales = Locale.getAvailableLocales();

        for(int i = 0; i < availableLocales.length; ++i) {
            Locale locale = availableLocales[i];
            AVAILABLE_LANGUAGES.add(locale.getLanguage());
            AVAILABLE_COUNTRIES.add(locale.getCountry());
        }

        systemLocaleInfo = new LocaleInfo();
        defaultLocalInfo = systemLocaleInfo;
    }
}
