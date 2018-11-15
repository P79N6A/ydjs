package com.yd.ydsp.common.lang;

/**
 * Created by zengyixun on 17/3/18.
 */

import com.yd.ydsp.common.lang.StringEscapeUtil;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.lang.i18n.LocaleUtil;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class SystemUtil {
    private static final SystemUtil.JvmSpecInfo JVM_SPEC_INFO = new SystemUtil.JvmSpecInfo();
    private static final SystemUtil.JvmInfo JVM_INFO = new SystemUtil.JvmInfo();
    private static final SystemUtil.JavaSpecInfo JAVA_SPEC_INFO = new SystemUtil.JavaSpecInfo();
    private static final SystemUtil.JavaInfo JAVA_INFO = new SystemUtil.JavaInfo();
    private static final SystemUtil.OsInfo OS_INFO = new SystemUtil.OsInfo();
    private static final SystemUtil.UserInfo USER_INFO = new SystemUtil.UserInfo();
    private static final SystemUtil.HostInfo HOST_INFO = new SystemUtil.HostInfo();
    private static final SystemUtil.JavaRuntimeInfo JAVA_RUNTIME_INFO = new SystemUtil.JavaRuntimeInfo();

    public SystemUtil() {
    }

    public static final SystemUtil.JvmSpecInfo getJvmSpecInfo() {
        return JVM_SPEC_INFO;
    }

    public static final SystemUtil.JvmInfo getJvmInfo() {
        return JVM_INFO;
    }

    public static final SystemUtil.JavaSpecInfo getJavaSpecInfo() {
        return JAVA_SPEC_INFO;
    }

    public static final SystemUtil.JavaInfo getJavaInfo() {
        return JAVA_INFO;
    }

    public static final SystemUtil.JavaRuntimeInfo getJavaRuntimeInfo() {
        return JAVA_RUNTIME_INFO;
    }

    public static final SystemUtil.OsInfo getOsInfo() {
        return OS_INFO;
    }

    public static final SystemUtil.UserInfo getUserInfo() {
        return USER_INFO;
    }

    public static final SystemUtil.HostInfo getHostInfo() {
        return HOST_INFO;
    }

    public static final void dumpSystemInfo() {
        dumpSystemInfo(new PrintWriter(System.out));
    }

    public static final void dumpSystemInfo(PrintWriter out) {
        out.println("--------------");
        out.println(getJvmSpecInfo());
        out.println("--------------");
        out.println(getJvmInfo());
        out.println("--------------");
        out.println(getJavaSpecInfo());
        out.println("--------------");
        out.println(getJavaInfo());
        out.println("--------------");
        out.println(getJavaRuntimeInfo());
        out.println("--------------");
        out.println(getOsInfo());
        out.println("--------------");
        out.println(getUserInfo());
        out.println("--------------");
        out.println(getHostInfo());
        out.println("--------------");
        out.flush();
    }

    private static String getSystemProperty(String name, boolean quiet) {
        try {
            return System.getProperty(name);
        } catch (SecurityException var3) {
            if(!quiet) {
                System.err.println("Caught a SecurityException reading the system property \'" + name + "\'; the SystemUtil property value will default to null.");
            }

            return null;
        }
    }

    private static void append(StringBuffer buffer, String caption, String value) {
        buffer.append(caption).append(StringUtil.defaultIfNull(StringEscapeUtil.escapeJava(value), "[n/a]")).append("\n");
    }

    public static void main(String[] args) {
        dumpSystemInfo();
        ArrayList list = new ArrayList(System.getProperties().keySet());
        Collections.sort(list);
        Iterator i = list.iterator();

        while(i.hasNext()) {
            String key = (String)i.next();
            String value = System.getProperty(key);
            System.out.println(key + " = " + StringUtil.defaultIfNull(StringEscapeUtil.escapeJava(value), "[n/a]"));
        }

    }

    public static final class HostInfo {
        private final String HOST_NAME;
        private final String HOST_ADDRESS;

        private HostInfo() {
            String hostName;
            String hostAddress;
            try {
                InetAddress e = InetAddress.getLocalHost();
                hostName = e.getHostName();
                hostAddress = e.getHostAddress();
            } catch (UnknownHostException var4) {
                hostName = "localhost";
                hostAddress = "127.0.0.1";
            }

            this.HOST_NAME = hostName;
            this.HOST_ADDRESS = hostAddress;
        }

        public final String getName() {
            return this.HOST_NAME;
        }

        public final String getAddress() {
            return this.HOST_ADDRESS;
        }

        public final String toString() {
            StringBuffer buffer = new StringBuffer();
            SystemUtil.append(buffer, "Host Name:    ", this.getName());
            SystemUtil.append(buffer, "Host Address: ", this.getAddress());
            return buffer.toString();
        }
    }

    public static final class UserInfo {
        private final String USER_NAME;
        private final String USER_HOME;
        private final String USER_DIR;
        private final String USER_LANGUAGE;
        private final String USER_COUNTRY;
        private final String JAVA_IO_TMPDIR;

        private UserInfo() {
            this.USER_NAME = SystemUtil.getSystemProperty("user.name", false);
            this.USER_HOME = SystemUtil.getSystemProperty("user.home", false);
            this.USER_DIR = SystemUtil.getSystemProperty("user.dir", false);
            this.USER_LANGUAGE = SystemUtil.getSystemProperty("user.language", false);
            this.USER_COUNTRY = SystemUtil.getSystemProperty("user.country", false) == null?SystemUtil.getSystemProperty("user.region", false):SystemUtil.getSystemProperty("user.country", false);
            this.JAVA_IO_TMPDIR = SystemUtil.getSystemProperty("java.io.tmpdir", false);
        }

        public final String getName() {
            return this.USER_NAME;
        }

        public final String getHomeDir() {
            return this.USER_HOME;
        }

        public final String getCurrentDir() {
            return this.USER_DIR;
        }

        public final String getTempDir() {
            return this.JAVA_IO_TMPDIR;
        }

        public final String getLanguage() {
            return this.USER_LANGUAGE;
        }

        public final String getCountry() {
            return this.USER_COUNTRY;
        }

        public final String toString() {
            StringBuffer buffer = new StringBuffer();
            SystemUtil.append(buffer, "User Name:        ", this.getName());
            SystemUtil.append(buffer, "User Home Dir:    ", this.getHomeDir());
            SystemUtil.append(buffer, "User Current Dir: ", this.getCurrentDir());
            SystemUtil.append(buffer, "User Temp Dir:    ", this.getTempDir());
            SystemUtil.append(buffer, "User Language:    ", this.getLanguage());
            SystemUtil.append(buffer, "User Country:     ", this.getCountry());
            return buffer.toString();
        }
    }

    public static final class OsInfo {
        private final String OS_VERSION;
        private final String OS_ARCH;
        private final String OS_NAME;
        private final boolean IS_OS_AIX;
        private final boolean IS_OS_HP_UX;
        private final boolean IS_OS_IRIX;
        private final boolean IS_OS_LINUX;
        private final boolean IS_OS_MAC;
        private final boolean IS_OS_MAC_OSX;
        private final boolean IS_OS_OS2;
        private final boolean IS_OS_SOLARIS;
        private final boolean IS_OS_SUN_OS;
        private final boolean IS_OS_WINDOWS;
        private final boolean IS_OS_WINDOWS_2000;
        private final boolean IS_OS_WINDOWS_95;
        private final boolean IS_OS_WINDOWS_98;
        private final boolean IS_OS_WINDOWS_ME;
        private final boolean IS_OS_WINDOWS_NT;
        private final boolean IS_OS_WINDOWS_XP;
        private final String FILE_ENCODING;
        private final String FILE_SEPARATOR;
        private final String LINE_SEPARATOR;
        private final String PATH_SEPARATOR;

        private OsInfo() {
            this.OS_VERSION = SystemUtil.getSystemProperty("os.version", false);
            this.OS_ARCH = SystemUtil.getSystemProperty("os.arch", false);
            this.OS_NAME = SystemUtil.getSystemProperty("os.name", false);
            this.IS_OS_AIX = this.getOSMatches("AIX");
            this.IS_OS_HP_UX = this.getOSMatches("HP-UX");
            this.IS_OS_IRIX = this.getOSMatches("Irix");
            this.IS_OS_LINUX = this.getOSMatches("Linux") || this.getOSMatches("LINUX");
            this.IS_OS_MAC = this.getOSMatches("Mac");
            this.IS_OS_MAC_OSX = this.getOSMatches("Mac OS X");
            this.IS_OS_OS2 = this.getOSMatches("OS/2");
            this.IS_OS_SOLARIS = this.getOSMatches("Solaris");
            this.IS_OS_SUN_OS = this.getOSMatches("SunOS");
            this.IS_OS_WINDOWS = this.getOSMatches("Windows");
            this.IS_OS_WINDOWS_2000 = this.getOSMatches("Windows", "5.0");
            this.IS_OS_WINDOWS_95 = this.getOSMatches("Windows 9", "4.0");
            this.IS_OS_WINDOWS_98 = this.getOSMatches("Windows 9", "4.1");
            this.IS_OS_WINDOWS_ME = this.getOSMatches("Windows", "4.9");
            this.IS_OS_WINDOWS_NT = this.getOSMatches("Windows NT");
            this.IS_OS_WINDOWS_XP = this.getOSMatches("Windows", "5.1");
            this.FILE_ENCODING = LocaleUtil.getSystem().getCharset();
            this.FILE_SEPARATOR = SystemUtil.getSystemProperty("file.separator", false);
            this.LINE_SEPARATOR = SystemUtil.getSystemProperty("line.separator", false);
            this.PATH_SEPARATOR = SystemUtil.getSystemProperty("path.separator", false);
        }

        public final String getArch() {
            return this.OS_ARCH;
        }

        public final String getName() {
            return this.OS_NAME;
        }

        public final String getVersion() {
            return this.OS_VERSION;
        }

        public final boolean isAix() {
            return this.IS_OS_AIX;
        }

        public final boolean isHpUx() {
            return this.IS_OS_HP_UX;
        }

        public final boolean isIrix() {
            return this.IS_OS_IRIX;
        }

        public final boolean isLinux() {
            return this.IS_OS_LINUX;
        }

        public final boolean isMac() {
            return this.IS_OS_MAC;
        }

        public final boolean isMacOsX() {
            return this.IS_OS_MAC_OSX;
        }

        public final boolean isOs2() {
            return this.IS_OS_OS2;
        }

        public final boolean isSolaris() {
            return this.IS_OS_SOLARIS;
        }

        public final boolean isSunOS() {
            return this.IS_OS_SUN_OS;
        }

        public final boolean isWindows() {
            return this.IS_OS_WINDOWS;
        }

        public final boolean isWindows2000() {
            return this.IS_OS_WINDOWS_2000;
        }

        public final boolean isWindows95() {
            return this.IS_OS_WINDOWS_95;
        }

        public final boolean isWindows98() {
            return this.IS_OS_WINDOWS_98;
        }

        public final boolean isWindowsME() {
            return this.IS_OS_WINDOWS_ME;
        }

        public final boolean isWindowsNT() {
            return this.IS_OS_WINDOWS_NT;
        }

        public final boolean isWindowsXP() {
            return this.IS_OS_WINDOWS_XP;
        }

        private final boolean getOSMatches(String osNamePrefix) {
            return this.OS_NAME == null?false:this.OS_NAME.startsWith(osNamePrefix);
        }

        private final boolean getOSMatches(String osNamePrefix, String osVersionPrefix) {
            return this.OS_NAME != null && this.OS_VERSION != null?this.OS_NAME.startsWith(osNamePrefix) && this.OS_VERSION.startsWith(osVersionPrefix):false;
        }

        public final String getFileEncoding() {
            return this.FILE_ENCODING;
        }

        public final String getFileSeparator() {
            return this.FILE_SEPARATOR;
        }

        public final String getLineSeparator() {
            return this.LINE_SEPARATOR;
        }

        public final String getPathSeparator() {
            return this.PATH_SEPARATOR;
        }

        public final String toString() {
            StringBuffer buffer = new StringBuffer();
            SystemUtil.append(buffer, "OS Arch:        ", this.getArch());
            SystemUtil.append(buffer, "OS Name:        ", this.getName());
            SystemUtil.append(buffer, "OS Version:     ", this.getVersion());
            SystemUtil.append(buffer, "File Encoding:  ", this.getFileEncoding());
            SystemUtil.append(buffer, "File Separator: ", this.getFileSeparator());
            SystemUtil.append(buffer, "Line Separator: ", this.getLineSeparator());
            SystemUtil.append(buffer, "Path Separator: ", this.getPathSeparator());
            return buffer.toString();
        }
    }

    public static final class JavaRuntimeInfo {
        private final String JAVA_RUNTIME_NAME;
        private final String JAVA_RUNTIME_VERSION;
        private final String JAVA_HOME;
        private final String JAVA_EXT_DIRS;
        private final String JAVA_ENDORSED_DIRS;
        private final String JAVA_CLASS_PATH;
        private final String JAVA_CLASS_VERSION;
        private final String JAVA_LIBRARY_PATH;

        private JavaRuntimeInfo() {
            this.JAVA_RUNTIME_NAME = SystemUtil.getSystemProperty("java.runtime.name", false);
            this.JAVA_RUNTIME_VERSION = SystemUtil.getSystemProperty("java.runtime.version", false);
            this.JAVA_HOME = SystemUtil.getSystemProperty("java.home", false);
            this.JAVA_EXT_DIRS = SystemUtil.getSystemProperty("java.ext.dirs", false);
            this.JAVA_ENDORSED_DIRS = SystemUtil.getSystemProperty("java.endorsed.dirs", false);
            this.JAVA_CLASS_PATH = SystemUtil.getSystemProperty("java.class.path", false);
            this.JAVA_CLASS_VERSION = SystemUtil.getSystemProperty("java.class.version", false);
            this.JAVA_LIBRARY_PATH = SystemUtil.getSystemProperty("java.library.path", false);
        }

        public final String getName() {
            return this.JAVA_RUNTIME_NAME;
        }

        public final String getVersion() {
            return this.JAVA_RUNTIME_VERSION;
        }

        public final String getHomeDir() {
            return this.JAVA_HOME;
        }

        public final String getExtDirs() {
            return this.JAVA_EXT_DIRS;
        }

        public final String getEndorsedDirs() {
            return this.JAVA_ENDORSED_DIRS;
        }

        public final String getClassPath() {
            return this.JAVA_CLASS_PATH;
        }

        public final String[] getClassPathArray() {
            return StringUtil.split(this.getClassPath(), SystemUtil.OS_INFO.getPathSeparator());
        }

        public final String getClassVersion() {
            return this.JAVA_CLASS_VERSION;
        }

        public final String getLibraryPath() {
            return this.JAVA_LIBRARY_PATH;
        }

        public final String[] getLibraryPathArray() {
            return StringUtil.split(this.getLibraryPath(), SystemUtil.OS_INFO.getPathSeparator());
        }

        public final String getProtocolPackages() {
            return SystemUtil.getSystemProperty("java.protocol.handler.pkgs", true);
        }

        public final String toString() {
            StringBuffer buffer = new StringBuffer();
            SystemUtil.append(buffer, "Java Runtime Name:      ", this.getName());
            SystemUtil.append(buffer, "Java Runtime Version:   ", this.getVersion());
            SystemUtil.append(buffer, "Java Home Dir:          ", this.getHomeDir());
            SystemUtil.append(buffer, "Java Extension Dirs:    ", this.getExtDirs());
            SystemUtil.append(buffer, "Java Endorsed Dirs:     ", this.getEndorsedDirs());
            SystemUtil.append(buffer, "Java Class Path:        ", this.getClassPath());
            SystemUtil.append(buffer, "Java Class Version:     ", this.getClassVersion());
            SystemUtil.append(buffer, "Java Library Path:      ", this.getLibraryPath());
            SystemUtil.append(buffer, "Java Protocol Packages: ", this.getProtocolPackages());
            return buffer.toString();
        }
    }

    public static final class JavaInfo {
        private final String JAVA_VERSION;
        private final float JAVA_VERSION_FLOAT;
        private final int JAVA_VERSION_INT;
        private final String JAVA_VENDOR;
        private final String JAVA_VENDOR_URL;
        private final boolean IS_JAVA_1_1;
        private final boolean IS_JAVA_1_2;
        private final boolean IS_JAVA_1_3;
        private final boolean IS_JAVA_1_4;
        private final boolean IS_JAVA_1_5;

        private JavaInfo() {
            this.JAVA_VERSION = SystemUtil.getSystemProperty("java.version", false);
            this.JAVA_VERSION_FLOAT = this.getJavaVersionAsFloat();
            this.JAVA_VERSION_INT = this.getJavaVersionAsInt();
            this.JAVA_VENDOR = SystemUtil.getSystemProperty("java.vendor", false);
            this.JAVA_VENDOR_URL = SystemUtil.getSystemProperty("java.vendor.url", false);
            this.IS_JAVA_1_1 = this.getJavaVersionMatches("1.1");
            this.IS_JAVA_1_2 = this.getJavaVersionMatches("1.2");
            this.IS_JAVA_1_3 = this.getJavaVersionMatches("1.3");
            this.IS_JAVA_1_4 = this.getJavaVersionMatches("1.4");
            this.IS_JAVA_1_5 = this.getJavaVersionMatches("1.5");
        }

        public final String getVersion() {
            return this.JAVA_VERSION;
        }

        public final float getVersionFloat() {
            return this.JAVA_VERSION_FLOAT;
        }

        public final int getVersionInt() {
            return this.JAVA_VERSION_INT;
        }

        private final float getJavaVersionAsFloat() {
            if(this.JAVA_VERSION == null) {
                return 0.0F;
            } else {
                String str = this.JAVA_VERSION.substring(0, 3);
                if(this.JAVA_VERSION.length() >= 5) {
                    str = str + this.JAVA_VERSION.substring(4, 5);
                }

                return Float.parseFloat(str);
            }
        }

        private final int getJavaVersionAsInt() {
            if(this.JAVA_VERSION == null) {
                return 0;
            } else {
                String str = this.JAVA_VERSION.substring(0, 1);
                str = str + this.JAVA_VERSION.substring(2, 3);
                if(this.JAVA_VERSION.length() >= 5) {
                    str = str + this.JAVA_VERSION.substring(4, 5);
                } else {
                    str = str + "0";
                }

                return Integer.parseInt(str);
            }
        }

        public final String getVendor() {
            return this.JAVA_VENDOR;
        }

        public final String getVendorURL() {
            return this.JAVA_VENDOR_URL;
        }

        public final boolean isJava11() {
            return this.IS_JAVA_1_1;
        }

        public final boolean isJava12() {
            return this.IS_JAVA_1_2;
        }

        public final boolean isJava13() {
            return this.IS_JAVA_1_3;
        }

        public final boolean isJava14() {
            return this.IS_JAVA_1_4;
        }

        public final boolean isJava15() {
            return this.IS_JAVA_1_5;
        }

        private final boolean getJavaVersionMatches(String versionPrefix) {
            return this.JAVA_VERSION == null?false:this.JAVA_VERSION.startsWith(versionPrefix);
        }

        public final boolean isJavaVersionAtLeast(float requiredVersion) {
            return this.getVersionFloat() >= requiredVersion;
        }

        public final boolean isJavaVersionAtLeast(int requiredVersion) {
            return this.getVersionInt() >= requiredVersion;
        }

        public final String toString() {
            StringBuffer buffer = new StringBuffer();
            SystemUtil.append(buffer, "Java Version:    ", this.getVersion());
            SystemUtil.append(buffer, "Java Vendor:     ", this.getVendor());
            SystemUtil.append(buffer, "Java Vendor URL: ", this.getVendorURL());
            return buffer.toString();
        }
    }

    public static final class JavaSpecInfo {
        private final String JAVA_SPECIFICATION_NAME;
        private final String JAVA_SPECIFICATION_VERSION;
        private final String JAVA_SPECIFICATION_VENDOR;

        private JavaSpecInfo() {
            this.JAVA_SPECIFICATION_NAME = SystemUtil.getSystemProperty("java.specification.name", false);
            this.JAVA_SPECIFICATION_VERSION = SystemUtil.getSystemProperty("java.specification.version", false);
            this.JAVA_SPECIFICATION_VENDOR = SystemUtil.getSystemProperty("java.specification.vendor", false);
        }

        public final String getName() {
            return this.JAVA_SPECIFICATION_NAME;
        }

        public final String getVersion() {
            return this.JAVA_SPECIFICATION_VERSION;
        }

        public final String getVendor() {
            return this.JAVA_SPECIFICATION_VENDOR;
        }

        public final String toString() {
            StringBuffer buffer = new StringBuffer();
            SystemUtil.append(buffer, "Java Spec. Name:    ", this.getName());
            SystemUtil.append(buffer, "Java Spec. Version: ", this.getVersion());
            SystemUtil.append(buffer, "Java Spec. Vendor:  ", this.getVendor());
            return buffer.toString();
        }
    }

    public static final class JvmInfo {
        private final String JAVA_VM_NAME;
        private final String JAVA_VM_VERSION;
        private final String JAVA_VM_VENDOR;
        private final String JAVA_VM_INFO;

        private JvmInfo() {
            this.JAVA_VM_NAME = SystemUtil.getSystemProperty("java.vm.name", false);
            this.JAVA_VM_VERSION = SystemUtil.getSystemProperty("java.vm.version", false);
            this.JAVA_VM_VENDOR = SystemUtil.getSystemProperty("java.vm.vendor", false);
            this.JAVA_VM_INFO = SystemUtil.getSystemProperty("java.vm.info", false);
        }

        public final String getName() {
            return this.JAVA_VM_NAME;
        }

        public final String getVersion() {
            return this.JAVA_VM_VERSION;
        }

        public final String getVendor() {
            return this.JAVA_VM_VENDOR;
        }

        public final String getInfo() {
            return this.JAVA_VM_INFO;
        }

        public final String toString() {
            StringBuffer buffer = new StringBuffer();
            SystemUtil.append(buffer, "JavaVM Name:    ", this.getName());
            SystemUtil.append(buffer, "JavaVM Version: ", this.getVersion());
            SystemUtil.append(buffer, "JavaVM Vendor:  ", this.getVendor());
            SystemUtil.append(buffer, "JavaVM Info:    ", this.getInfo());
            return buffer.toString();
        }
    }

    public static final class JvmSpecInfo {
        private final String JAVA_VM_SPECIFICATION_NAME;
        private final String JAVA_VM_SPECIFICATION_VERSION;
        private final String JAVA_VM_SPECIFICATION_VENDOR;

        private JvmSpecInfo() {
            this.JAVA_VM_SPECIFICATION_NAME = SystemUtil.getSystemProperty("java.vm.specification.name", false);
            this.JAVA_VM_SPECIFICATION_VERSION = SystemUtil.getSystemProperty("java.vm.specification.version", false);
            this.JAVA_VM_SPECIFICATION_VENDOR = SystemUtil.getSystemProperty("java.vm.specification.vendor", false);
        }

        public final String getName() {
            return this.JAVA_VM_SPECIFICATION_NAME;
        }

        public final String getVersion() {
            return this.JAVA_VM_SPECIFICATION_VERSION;
        }

        public final String getVendor() {
            return this.JAVA_VM_SPECIFICATION_VENDOR;
        }

        public final String toString() {
            StringBuffer buffer = new StringBuffer();
            SystemUtil.append(buffer, "JavaVM Spec. Name:    ", this.getName());
            SystemUtil.append(buffer, "JavaVM Spec. Version: ", this.getVersion());
            SystemUtil.append(buffer, "JavaVM Spec. Vendor:  ", this.getVendor());
            return buffer.toString();
        }
    }
}
