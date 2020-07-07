package com.doys.framework.util;
public class UtilFile {
    public static String getNewName(String originalName, String replaceName) {
        String extName = getExtName(originalName);
        if (extName.equals("")) {
            return originalName;
        }
        return replaceName + "." + extName;
    }
    public static String getExtName(String filename) {
        int idx = filename.lastIndexOf(".");
        if (idx > 0) {
            return filename.substring(idx + 1);
        }
        return "";
    }
}