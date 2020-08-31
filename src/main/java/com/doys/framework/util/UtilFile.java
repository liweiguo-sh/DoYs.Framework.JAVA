package com.doys.framework.util;
import java.io.*;
import java.util.ArrayList;

public class UtilFile {
    // ------------------------------------------------------------------------
    private static int fileSN = 1;
    synchronized public static String getFileSN() {
        return String.valueOf(fileSN++);
    }
    // ------------------------------------------------------------------------
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

    // -- charset -------------------------------------------------------------
    public static String getTxtFileCharset(String filePathName) {
        String charset = "";
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
        if (!charset.equals("")) {
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
        else if (charset.equalsIgnoreCase("GBK")) {
            // -- do nothing --
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

    // -- path ----------------------------------------------------------------
    public static boolean checkPath(String strTarget) throws Exception {
        return checkPath(strTarget, true);
    }
    public static boolean checkPath(String strTarget, boolean autoCreate) throws Exception {
        boolean blResult = false;
        int nIndex = 0;

        String strPath = "";
        File file = null;
        // ------------------------------------------------
        file = new File(strTarget);
        if (file.isDirectory()) {
            return true;
        }
        if (!autoCreate) {
            return false;
        }
        // ------------------------------------------------

        strTarget = strTarget.replaceAll("\\\\", "/");
        if (!strTarget.endsWith("/")) {
            strTarget += "/";
        }
        // ------------------------------------------------
        nIndex = strTarget.indexOf("/");
        while (nIndex > 0) {
            strPath = strTarget.substring(0, nIndex + 1);
            file = new File(strPath);
            if (file.isDirectory()) {
                nIndex = strTarget.indexOf("/", nIndex + 1);
            }
            else {
                blResult = file.mkdir();
                if (blResult == false) {
                    throw new Exception("路径创建失败。" + strPath);
                }
                nIndex = strTarget.indexOf("/", nIndex + 1);
            }
        }
        // ------------------------------------------------
        return true;
    }

    // -- write file ----------------------------------------------------------
    public static void writeFile(String path, ArrayList<String> list) throws Exception {
        writeFile(path, list, "\r\n");
    }
    public static void writeFile(String path, ArrayList<String> list, String appendRN) throws Exception {
        int nRows = list.size();

        FileOutputStream fos = new FileOutputStream(path);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        // ------------------------------------------------
        if (nRows > 0) {
            for (int i = 0; i < nRows - 1; i++) {
                osw.write(list.get(i) + appendRN);
            }
            osw.write(list.get(nRows - 1));
        }
        osw.flush();
    }
}