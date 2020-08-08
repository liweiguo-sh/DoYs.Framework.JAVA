package com.doys.framework.util;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

    public static ArrayList<String> readTextFile(String filename, String charset, int maxLine) throws Exception {
        boolean isFirstLine = true;

        String lineString;

        ArrayList<String> list = new ArrayList<>();
        // ------------------------------------------------
        if (charset.equals("")) {
            charset = getTxtFileCharset(filename);
        }

        try (FileInputStream fis = new FileInputStream(filename);
             InputStreamReader isr = new InputStreamReader(fis, charset);
             BufferedReader bufReader = new BufferedReader(isr)) {
            while ((lineString = bufReader.readLine()) != null && (maxLine--) != 0) {
                if (isFirstLine) {
                    lineString = removeBOM(lineString, charset);
                    isFirstLine = false;
                }

                list.add(lineString);
            }
        }
        return list;
    }

    public static String getTxtFileCharset(String filePathName) {
        String charset = "utf-8";
        String strStart = "";

        byte[] byt = new byte[10];
        FileInputStream fis = null;
        // ------------------------------------------------
        try {
            fis = new FileInputStream(filePathName);
            fis.read(byt, 0, byt.length);
            strStart = byteArrayToHex(byt);

            if (strStart.startsWith("EFBBBF")) {
                charset = "utf-8";
            }
            else if (strStart.startsWith("FFFE")) {
                charset = "Unicode";
            }
            else if (strStart.startsWith("FEFF00")) {
                charset = "UTF-16";
            }
            else {
                // -- 无BOM的UTF-8也是没有起始特征码的，和gbk一样，暂无好办法判断。可以要求utf格式文件必需带BOM头。 --
                charset = "GBK"; // -- gbk编码前面没有特征码，gbk是gb2312的扩展，支持繁体、日文等 --
            }
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charset;
    }
    public static String removeBOM(String text, String charset) {
        String strStart = "";

        byte[] byt;
        // ------------------------------------------------
        if (charset.equals("")) {
            byt = text.getBytes();
            strStart = byteArrayToHex(byt);
            if (strStart.startsWith("EFBBBF")) {
                charset = "utf-8";
            }
            else if (strStart.startsWith("FFFE")) {
                charset = "Unicode";
            }
            else if (strStart.startsWith("FEFF00")) {
                charset = "UTF-16";
            }
            else {
                // -- 无BOM的UTF-8也是没有起始特征码的，和gbk一样，暂无好办法判断。可以要求utf格式文件必需带BOM头。 --
                charset = "GBK"; // -- gbk编码前面没有特征码，gbk是gb2312的扩展，支持繁体、日文等 --
            }
        }

        // ------------------------------------------------
        if (charset.equalsIgnoreCase("utf-8")) {
            text = text.substring(1);
        }
        else {
            throw new UnsupportedOperationException("removeBOM:  " + charset);
        }
        return text;
    }
    public static String byteArrayToHex(byte[] byteArray) {
        int i, len = byteArray.length;

        StringBuffer buf = new StringBuffer("");
        // ------------------------------------------------
        for (int offset = 0; offset < len; offset++) {
            i = byteArray[offset];
            if (i < 0) {
                i += 256;
            }
            if (i < 16) {
                buf.append("0");
            }
            buf.append(Integer.toHexString(i));
        }
        return buf.toString().toUpperCase();
    }

}