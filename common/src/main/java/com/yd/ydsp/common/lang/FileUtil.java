package com.yd.ydsp.common.lang;

/**
 * Created by zengyixun on 17/3/18.
 */

import com.yd.ydsp.common.lang.StringEscapeUtil;
import com.yd.ydsp.common.lang.StringUtil;
import com.yd.ydsp.common.lang.SystemUtil;
import java.io.File;
import java.net.URL;
import java.util.StringTokenizer;

public class FileUtil {
    private static final char COLON_CHAR = ':';
    private static final String UNC_PREFIX = "//";
    private static final String SLASH = "/";
    private static final String BACKSLASH = "\\";
    private static final char SLASH_CHAR = '/';
    private static final char BACKSLASH_CHAR = '\\';
    private static final String ALL_SLASH = "/\\";
    public static final String EXTENSION_SEPARATOR = ".";
    public static final String CURRENT_DIR = ".";
    public static final String UP_LEVEL_DIR = "..";

    public FileUtil() {
    }

    public static String normalizeAbsolutePath(String path) {
        String normalizedPath = normalizePath(path, false);
        if(normalizedPath != null && !normalizedPath.startsWith("/")) {
            if(!normalizedPath.equals(".") && !normalizedPath.equals("./")) {
                if(normalizedPath.startsWith("..")) {
                    normalizedPath = null;
                } else {
                    normalizedPath = '/' + normalizedPath;
                }
            } else {
                normalizedPath = "/";
            }
        }

        return normalizedPath;
    }

    public static String normalizePath(String path) {
        return normalizePath(path, SystemUtil.getOsInfo().isWindows());
    }

    public static String normalizeWindowsPath(String path) {
        return normalizePath(path, true);
    }

    public static String normalizeUnixPath(String path) {
        return normalizePath(path, false);
    }

    private static String normalizePath(String path, boolean isWindows) {
        if(path == null) {
            return null;
        } else {
            path = path.trim();
            path = path.replace('\\', '/');
            String prefix = getSystemDependentPrefix(path, isWindows);
            if(prefix == null) {
                return null;
            } else {
                path = path.substring(prefix.length());
                if(prefix.length() > 0 || path.startsWith("/")) {
                    prefix = prefix + '/';
                }

                boolean endsWithSlash = path.endsWith("/");
                StringTokenizer tokenizer = new StringTokenizer(path, "/");
                StringBuffer buffer = new StringBuffer(prefix.length() + path.length());
                int level = 0;
                buffer.append(prefix);

                while(tokenizer.hasMoreTokens()) {
                    String element = tokenizer.nextToken();
                    if(!".".equals(element)) {
                        if("..".equals(element)) {
                            if(level == 0) {
                                if(prefix.length() > 0) {
                                    return null;
                                }

                                buffer.append("..").append('/');
                            } else {
                                --level;
                                boolean found = false;

                                for(int i = buffer.length() - 2; i >= prefix.length(); --i) {
                                    if(buffer.charAt(i) == 47) {
                                        buffer.setLength(i + 1);
                                        found = true;
                                        break;
                                    }
                                }

                                if(!found) {
                                    buffer.setLength(prefix.length());
                                }
                            }
                        } else {
                            buffer.append(element).append('/');
                            ++level;
                        }
                    }
                }

                if(buffer.length() == 0) {
                    buffer.append(".").append('/');
                }

                if(!endsWithSlash && buffer.length() > prefix.length() && buffer.charAt(buffer.length() - 1) == 47) {
                    buffer.setLength(buffer.length() - 1);
                }

                return buffer.toString();
            }
        }
    }

    private static String getSystemDependentPrefix(String path, boolean isWindows) {
        if(isWindows) {
            if(path.startsWith("//")) {
                if(path.length() == "//".length()) {
                    return null;
                }

                int index = path.indexOf("/", "//".length());
                if(index != -1) {
                    return path.substring(0, index);
                }

                return path;
            }

            if(path.length() > 1 && path.charAt(1) == 58) {
                return path.substring(0, 2).toUpperCase();
            }
        }

        return "";
    }

    public static String getPathBasedOn(String basedir, String path) {
        return getPathBasedOn(basedir, path, SystemUtil.getOsInfo().isWindows());
    }

    public static String getWindowsPathBasedOn(String basedir, String path) {
        return getPathBasedOn(basedir, path, true);
    }

    public static String getUnixPathBasedOn(String basedir, String path) {
        return getPathBasedOn(basedir, path, false);
    }

    private static String getPathBasedOn(String basedir, String path, boolean isWindows) {
        if(path == null) {
            return null;
        } else {
            path = path.trim();
            path = path.replace('\\', '/');
            String prefix = getSystemDependentPrefix(path, isWindows);
            if(prefix == null) {
                return null;
            } else if(prefix.length() <= 0 && (path.length() <= prefix.length() || path.charAt(prefix.length()) != 47)) {
                if(basedir == null) {
                    return null;
                } else {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(basedir.trim());
                    if(basedir.length() > 0 && path.length() > 0 && basedir.charAt(basedir.length() - 1) != 47) {
                        buffer.append('/');
                    }

                    buffer.append(path);
                    return normalizePath(buffer.toString(), isWindows);
                }
            } else {
                return normalizePath(path, isWindows);
            }
        }
    }

    public static String getRelativePath(String basedir, String path) {
        return getRelativePath(basedir, path, SystemUtil.getOsInfo().isWindows());
    }

    public static String getWindowsRelativePath(String basedir, String path) {
        return getRelativePath(basedir, path, true);
    }

    public static String getUnixRelativePath(String basedir, String path) {
        return getRelativePath(basedir, path, false);
    }

    private static String getRelativePath(String basedir, String path, boolean isWindows) {
        basedir = normalizePath(basedir, isWindows);
        if(basedir == null) {
            return null;
        } else {
            String basePrefix = getSystemDependentPrefix(basedir, isWindows);
            if(basePrefix != null && (basePrefix.length() != 0 || basedir.startsWith("/"))) {
                path = getPathBasedOn(basedir, path, isWindows);
                if(path == null) {
                    return null;
                } else {
                    String prefix = getSystemDependentPrefix(path, isWindows);
                    if(!basePrefix.equals(prefix)) {
                        return path;
                    } else {
                        boolean endsWithSlash = path.endsWith("/");
                        String[] baseParts = StringUtil.split(basedir.substring(basePrefix.length()), '/');
                        String[] parts = StringUtil.split(path.substring(prefix.length()), '/');
                        StringBuffer buffer = new StringBuffer();
                        int i = 0;
                        if(isWindows) {
                            while(i < baseParts.length && i < parts.length && baseParts[i].equalsIgnoreCase(parts[i])) {
                                ++i;
                            }
                        } else {
                            while(i < baseParts.length && i < parts.length && baseParts[i].equals(parts[i])) {
                                ++i;
                            }
                        }

                        if(i < baseParts.length && i < parts.length) {
                            for(int relpath = i; relpath < baseParts.length; ++relpath) {
                                buffer.append("..").append('/');
                            }
                        }

                        for(; i < parts.length; ++i) {
                            buffer.append(parts[i]);
                            if(i < parts.length - 1) {
                                buffer.append('/');
                            }
                        }

                        if(buffer.length() == 0) {
                            buffer.append(".");
                        }

                        String var11 = buffer.toString();
                        if(endsWithSlash && !var11.endsWith("/")) {
                            var11 = var11 + "/";
                        }

                        return var11;
                    }
                }
            } else {
                return null;
            }
        }
    }

    public static File toFile(URL url) {
        if(url == null) {
            return null;
        } else {
            if(url.getProtocol().equals("file")) {
                String path = url.getPath();
                if(path != null) {
                    return new File(StringEscapeUtil.unescapeURL(path));
                }
            }

            return null;
        }
    }

    public static String[] parseExtension(String path) {
        path = StringUtil.trimToEmpty(path);
        String[] parts = new String[]{path, null};
        if(StringUtil.isEmpty(path)) {
            return parts;
        } else {
            int index = StringUtil.lastIndexOf(path, ".");
            String extension = null;
            if(index >= 0) {
                extension = StringUtil.trimToNull(StringUtil.substring(path, index + 1));
                if(!StringUtil.containsNone(extension, "/\\")) {
                    extension = null;
                    index = -1;
                }
            }

            if(index >= 0) {
                parts[0] = StringUtil.substring(path, 0, index);
            }

            parts[1] = extension;
            return parts;
        }
    }
}
