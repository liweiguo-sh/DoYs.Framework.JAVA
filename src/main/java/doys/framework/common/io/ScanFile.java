package doys.framework.common.io;
import doys.framework.a2.structure.EntityFile;
import doys.framework.util.UtilFile;

import java.util.ArrayList;

public class ScanFile {
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

            entityFiles = UtilFile.scan(folderPath, "class", includeSubDirectory);
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