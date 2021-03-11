package doys.framework.common.io;
import doys.framework.common.entity.EntityFile;
import doys.framework.core.ex.CommonException;

import java.io.File;
import java.util.ArrayList;

public class ScanFile {
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
                throw new CommonException("debug here ScanFile.scanFolder()");
            }
        }
        // -- 返回结果集 --
        return entityFiles;
    }

    public static ArrayList<String> scanClass(String entityPackages, boolean includeSubDirectory) throws Exception {
        int idx;

        String[] arrPackage;
        String classFile;

        ArrayList<String> arrClass = new ArrayList<>();
        ArrayList<EntityFile> entityFiles;
        // ------------------------------------------------
        arrPackage = entityPackages.replaceAll(" ", "").replaceAll(",", ";").replaceAll("，", ";").replaceAll("；", ";").split(";");

        for (String folderPath : arrPackage) {
            if (folderPath.startsWith("-")) {
                continue;
            }

            entityFiles = ScanFile.scan(folderPath, "class", includeSubDirectory);
            for (EntityFile entityFile : entityFiles) {
                classFile = entityFile.fullname;

                idx = classFile.indexOf("\\classes\\");
                classFile = classFile.substring(idx + 9);
                classFile = classFile.substring(0, classFile.length() - 6);
                classFile = classFile.replaceAll("\\\\", ".");

                arrClass.add(classFile);
            }
        }
        return arrClass;
    }
}