package com.yd.ydsp.common.lang.i18n;

/**
 * Created by zengyixun on 17/3/18.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public class CharsetMap {
    public static final String DEFAULT_CHARSET = "ISO-8859-1";
    public static final String CHARSET_RESOURCE = "charset.properties";
    private static final int MAP_CACHE = 0;
    private static final int MAP_PROG = 1;
    private static final int MAP_HOME = 2;
    private static final int MAP_SYS = 3;
    private static final int MAP_JAR = 4;
    private static final int MAP_COM = 5;
    private static HashMap commonMapper = new HashMap();
    private Map[] mappers;

    protected static Map loadStream(InputStream input) throws IOException {
        Properties props = new Properties();
        props.load(input);
        return new HashMap(props);
    }

    protected static Map loadFile(File file) throws IOException {
        return loadStream(new FileInputStream(file));
    }

    protected static Map loadPath(String path) throws IOException {
        return loadFile(new File(path));
    }

    protected static Map loadResource(String name) {
        InputStream input = CharsetMap.class.getResourceAsStream(name);
        if(input != null) {
            try {
                return loadStream(input);
            } catch (IOException var3) {
                return null;
            }
        } else {
            return null;
        }
    }

    public CharsetMap() {
        this.mappers = new Map[6];

        String path;
        try {
            path = System.getProperty("user.home");
            if(path != null) {
                path = path + File.separator + "charset.properties";
                this.mappers[2] = loadPath(path);
            }
        } catch (Exception var4) {
            ;
        }

        try {
            path = System.getProperty("java.home") + File.separator + "lib" + File.separator + "charset.properties";
            this.mappers[3] = loadPath(path);
        } catch (Exception var3) {
            ;
        }

        this.mappers[4] = loadResource("/META-INF/charset.properties");
        this.mappers[5] = commonMapper;
        this.mappers[0] = new Hashtable();
    }

    public CharsetMap(Properties props) {
        this();
        this.mappers[1] = new HashMap(props);
    }

    public CharsetMap(InputStream input) throws IOException {
        this();
        this.mappers[1] = loadStream(input);
    }

    public CharsetMap(File file) throws IOException {
        this();
        this.mappers[1] = loadFile(file);
    }

    public CharsetMap(String path) throws IOException {
        this();
        this.mappers[1] = loadPath(path);
    }

    public synchronized void setCharSet(String key, String charset) {
        HashMap mapper = (HashMap)this.mappers[1];
        mapper = mapper != null?(HashMap)mapper.clone():new HashMap();
        mapper.put(key, charset);
        this.mappers[1] = mapper;
        this.mappers[0].clear();
    }

    public String getCharSet(Locale locale) {
        String key = locale.toString();
        if(key.length() == 0) {
            key = "__" + locale.getVariant();
            if(key.length() == 2) {
                return "ISO-8859-1";
            }
        }

        String charset = this.searchCharSet((String)key);
        if(charset.length() == 0) {
            String[] items = new String[]{locale.getLanguage(), locale.getCountry(), locale.getVariant()};
            charset = this.searchCharSet((String[])items);
            if(charset.length() == 0) {
                charset = "ISO-8859-1";
            }

            this.mappers[0].put(key, charset);
        }

        return charset;
    }

    public String getCharSet(Locale locale, String variant) {
        if(variant != null && variant.length() > 0) {
            String key = locale.toString();
            if(key.length() == 0) {
                key = "__" + locale.getVariant();
                if(key.length() > 2) {
                    key = key + '_' + variant;
                } else {
                    key = key + variant;
                }
            } else if(locale.getCountry().length() == 0) {
                key = key + "__" + variant;
            } else {
                key = key + '_' + variant;
            }

            String charset = this.searchCharSet((String)key);
            if(charset.length() == 0) {
                String[] items = new String[]{locale.getLanguage(), locale.getCountry(), locale.getVariant(), variant};
                charset = this.searchCharSet((String[])items);
                if(charset.length() == 0) {
                    charset = "ISO-8859-1";
                }

                this.mappers[0].put(key, charset);
            }

            return charset;
        } else {
            return this.getCharSet((Locale)locale);
        }
    }

    public String getCharSet(String key) {
        String charset = this.searchCharSet((String)key);
        return charset.length() > 0?charset:"ISO-8859-1";
    }

    public String getCharSet(String key, String def) {
        String charset = this.searchCharSet((String)key);
        return charset.length() > 0?charset:def;
    }

    private String searchCharSet(String[] items) {
        StringBuffer sb = new StringBuffer();

        for(int i = items.length; i > 0; --i) {
            String charset = this.searchCharSet(items, sb, i);
            if(charset.length() > 0) {
                return charset;
            }

            sb.setLength(0);
        }

        return "";
    }

    private String searchCharSet(String[] items, StringBuffer base, int count) {
        --count;
        if(count >= 0 && items[count] != null && items[count].length() > 0) {
            base.insert(0, items[count]);
            int length = base.length();

            for(int i = count; i > 0; --i) {
                if(i == count || i <= 1) {
                    base.insert(0, '_');
                    ++length;
                }

                String charset = this.searchCharSet(items, base, i);
                if(charset.length() > 0) {
                    return charset;
                }

                base.delete(0, base.length() - length);
            }

            return this.searchCharSet((String)base.toString());
        } else {
            return "";
        }
    }

    private String searchCharSet(String key) {
        if(key != null && key.length() > 0) {
            for(int i = 0; i < this.mappers.length; ++i) {
                Map mapper = this.mappers[i];
                if(mapper != null) {
                    String charset = (String)mapper.get(key);
                    if(charset != null) {
                        if(i > 0) {
                            this.mappers[0].put(key, charset);
                        }

                        return charset;
                    }
                }
            }

            this.mappers[0].put(key, "");
        }

        return "";
    }

    protected synchronized void setCommonCharSet(String key, String charset) {
        HashMap mapper = (HashMap)((HashMap)this.mappers[5]).clone();
        mapper.put(key, charset);
        this.mappers[5] = mapper;
        this.mappers[0].clear();
    }

    static {
        commonMapper.put("ar", "ISO-8859-6");
        commonMapper.put("be", "ISO-8859-5");
        commonMapper.put("bg", "ISO-8859-5");
        commonMapper.put("ca", "ISO-8859-1");
        commonMapper.put("cs", "ISO-8859-2");
        commonMapper.put("da", "ISO-8859-1");
        commonMapper.put("de", "ISO-8859-1");
        commonMapper.put("el", "ISO-8859-7");
        commonMapper.put("en", "ISO-8859-1");
        commonMapper.put("es", "ISO-8859-1");
        commonMapper.put("et", "ISO-8859-1");
        commonMapper.put("fi", "ISO-8859-1");
        commonMapper.put("fr", "ISO-8859-1");
        commonMapper.put("hr", "ISO-8859-2");
        commonMapper.put("hu", "ISO-8859-2");
        commonMapper.put("is", "ISO-8859-1");
        commonMapper.put("it", "ISO-8859-1");
        commonMapper.put("iw", "ISO-8859-8");
        commonMapper.put("ja", "Shift_JIS");
        commonMapper.put("ko", "EUC-KR");
        commonMapper.put("lt", "ISO-8859-2");
        commonMapper.put("lv", "ISO-8859-2");
        commonMapper.put("mk", "ISO-8859-5");
        commonMapper.put("nl", "ISO-8859-1");
        commonMapper.put("no", "ISO-8859-1");
        commonMapper.put("pl", "ISO-8859-2");
        commonMapper.put("pt", "ISO-8859-1");
        commonMapper.put("ro", "ISO-8859-2");
        commonMapper.put("ru", "ISO-8859-5");
        commonMapper.put("sh", "ISO-8859-5");
        commonMapper.put("sk", "ISO-8859-2");
        commonMapper.put("sl", "ISO-8859-2");
        commonMapper.put("sq", "ISO-8859-2");
        commonMapper.put("sr", "ISO-8859-5");
        commonMapper.put("sv", "ISO-8859-1");
        commonMapper.put("tr", "ISO-8859-9");
        commonMapper.put("uk", "ISO-8859-5");
        commonMapper.put("zh", "GB18030");
        commonMapper.put("zh_TW", "Big5");
    }
}
