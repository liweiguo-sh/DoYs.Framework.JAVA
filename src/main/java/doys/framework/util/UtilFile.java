package doys.framework.util;
import doys.framework.a2.structure.EntityFile;
import doys.framework.core.ex.CommonException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
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
    public static String removePath(String pathname) {
        int idx;
        String name = pathname;

        idx = pathname.lastIndexOf("\\");
        if (idx < 0) {
            idx = pathname.lastIndexOf("/");
        }

        if (idx >= 0) {
            name = pathname.substring(idx + 1);
        }
        return name;
    }
    public static String getSizeText(long size) {
        float sizeValue = size;
        String unit = "";

        if (sizeValue < 1024) {
            unit = "B";
        }
        else {
            sizeValue = sizeValue / 1024;
            if (sizeValue < 1024) {
                unit = "K";
            }
            else {
                sizeValue = sizeValue / 1024;
                if (sizeValue < 1024) {
                    unit = "M";
                }
                else {
                    sizeValue = sizeValue / 1024;
                    if (sizeValue < 1024) {
                        unit = "G";
                    }
                }
            }
        }
        return new DecimalFormat("#0.##").format(sizeValue) + unit;
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
                    throw new CommonException("路径创建失败。" + strPath);
                }
                nIndex = strTarget.indexOf("/", nIndex + 1);
            }
        }
        // ------------------------------------------------
        return true;
    }
    public static boolean emptyPath(String path) {
        // -- 删除文件夹下面的所有文件、子文件夹(递归删除子文件下面的文件及子文件夹)，当前文件夹不删除 --
        File file = new File(path);

        if (!file.exists()) return true;
        if (file.isFile()) {
            return false;
        }
        deleteFileAndPath(file);

        if (file.listFiles().length == 0) {
            return true;
        }
        return false;
    }
    private static void deleteFileAndPath(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFileAndPath(f);
                }
                f.delete();
            }
        }
    }

    public static String Combine(String path, String filename) {
        if (path.endsWith("/") || path.endsWith("\\")) {
            return path + filename;
        }
        else {
            return path + "/" + filename;
        }
    }

    // -- scan directory and files --------------------------------------------
    public static ArrayList<EntityFile> scan(String folderPath, String extNames, boolean includeSubDirectory) throws Exception {
        File fileFolder = new File(folderPath);
        if (!fileFolder.exists()) {
            throw new CommonException("the directory " + folderPath + " does not exist");
        }
        if (!fileFolder.isDirectory()) {
            throw new Exception(folderPath + " is not a file directory");
        }

        return scanFolder(fileFolder, extNames, includeSubDirectory);
    }
    private static ArrayList<EntityFile> scanFolder(File fileFolder, String extNames, boolean includeSubDirectory) throws Exception {
        ArrayList<EntityFile> entityFiles = new ArrayList<>();
        String[] arrExtName = extNames.replaceAll(" ", "").split(",");
        File[] files = fileFolder.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                boolean matchExtName = true;
                EntityFile entityFile = new EntityFile(file);
                if (extNames != null && !extNames.equals("")) {
                    matchExtName = false;
                    for (String extname : arrExtName) {
                        if (entityFile.extname.equalsIgnoreCase(extname)) {
                            matchExtName = true;
                            break;
                        }
                    }
                }
                if (!matchExtName) {
                    continue;
                }

                entityFiles.add(entityFile);
            }
            else if (file.isDirectory()) {
                entityFiles.addAll(scanFolder(new File(file.getPath()), extNames, includeSubDirectory));
            }
            else {
                throw new CommonException("debug here ScanFile.scanFolder()");
            }
        }
        // -- 返回结果集 --
        return entityFiles;
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

    // -- copy\delete file ----------------------------------------------------
    public static boolean copyFile(String fileSource, String fildDestination) throws Exception {
        FileInputStream fisSrc;
        FileOutputStream fosDst;
        FileChannel srcChannel, dstChannel;
        // ------------------------------------------------
        if (!(new File(fileSource).exists())) return false;

        checkPath(new File(fildDestination).getParent(), true);

        fisSrc = new FileInputStream(fileSource);
        fosDst = new FileOutputStream(fildDestination);

        srcChannel = fisSrc.getChannel();
        dstChannel = fosDst.getChannel();

        dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
        // ------------------------------------------------
        srcChannel.close();
        dstChannel.close();

        fisSrc.close();
        fosDst.close();
        // ------------------------------------------------
        return true;
    }
    public static void copyDirectory(String dirSource, String dirDestination) throws Exception {
        int len = dirSource.length();

        String fullnameSource, fullnameDestination;
        EntityFile entityFile;
        ArrayList<EntityFile> listSourceFiles;

        // ------------------------------------------------
        UtilFile.checkPath(dirDestination, true);

        listSourceFiles = scan(dirSource, "", true);
        for (int i = 0; i < listSourceFiles.size(); i++) {
            entityFile = listSourceFiles.get(i);
            fullnameSource = entityFile.fullname;
            fullnameDestination = dirDestination + fullnameSource.substring(len);
            UtilFile.copyFile(fullnameSource, fullnameDestination);
        }
    }

    public static boolean deleteFile(String fileDelete) throws Exception {
        File file = new File(fileDelete);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    // -- upload file ---------------------------------------------------------
    public static String saveUploadFile(MultipartFile multipartFile, String relativePath, String name) throws Exception {
        String path, pathname;

        File file;
        // -- 1. path -------------------------------------
        path = UtilYml.getRunPath(relativePath);
        file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new CommonException("创建文件目录失败，请检查。");
            }
        }

        // -- 2. pathname ---------------------------------
        pathname = Combine(path, name);

        // -- 3. save -------------------------------------
        file = new File(pathname);
        multipartFile.transferTo(file);

        return pathname;
    }
}