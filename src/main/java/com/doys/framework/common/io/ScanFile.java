package com.doys.framework.common.io;
import com.doys.framework.common.entity.EntityFile;
import com.doys.framework.util.UtilEnv;

import java.io.File;
import java.util.ArrayList;

public class ScanFile {
    public static ArrayList<EntityFile> scan(String folderPath, String extNames, boolean includeSubDirectory) throws Exception {
        File fileFolder = new File(folderPath);
        if (!fileFolder.exists()) {
            throw new Exception("the directory " + folderPath + " does not exist");
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
                if (extNames != null) {
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
                throw new Exception("debug here ScanFile.scanFolder()");
            }
        }
        // -- 返回结果集 --
        return entityFiles;
    }

    public static ArrayList<String> scanClass(String entityPackages, boolean includeSubDirectory) throws Exception {
        int nLen;

        String[] arrPackage;
        String classRoot, classFile, folderPath;

        ArrayList<String> arrClass = new ArrayList<>();
        ArrayList<EntityFile> entityFiles;
        // ------------------------------------------------
        arrPackage = entityPackages.replaceAll(" ", "").replaceAll(",", ";").replaceAll("，", ";").replaceAll("；", ";").split(";");
        classRoot = UtilEnv.getPackagePath("");
        nLen = classRoot.length();

        for (String packageName : arrPackage) {
            if (packageName.startsWith("-")) {
                continue;
            }
            folderPath = UtilEnv.getPackagePath(packageName);

            entityFiles = ScanFile.scan(folderPath, "class", includeSubDirectory);
            for (EntityFile entityFile : entityFiles) {
                if (entityFile.path.indexOf("\\dts\\") <= 0) {
                    continue;
                }

                classFile = entityFile.fullname;
                classFile = classFile.substring(nLen);
                classFile = classFile.substring(0, classFile.length() - 6);
                classFile = classFile.replaceAll("\\\\", ".");

                arrClass.add(classFile);
            }
        }
        // ------------------------------------------------
        return arrClass;
    }
}