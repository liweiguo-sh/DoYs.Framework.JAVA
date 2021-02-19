package doys.framework.common.entity;

import java.io.File;

/**
 * 文件实体类
 * <br>示例：C:\path1\subpath2\xxx\abc.jpg
 * <br>name: abc
 * <br>extname: jpg
 * <br>filename: abc.jpg
 * <br>fullname: C:\path1\subpath2\xxx\abc.jpg
 * <br>path: C:\path1\subpath2\xxx\
 */
public class EntityFile {
    public String name = "";
    public String extname = "";
    public String filename = "";
    public String fullname = "";

    public String path = "";

    public double KB = 0;
    public double MB = 0;

    /**
     * 摘要
     */
    public String digest = "";

    public EntityFile(File file) {
        filename = file.getName();

        int pos = filename.lastIndexOf(".");
        if (pos > 0) {
            name = filename.substring(0, pos);
            extname = filename.substring(pos + 1);
        } else {
            name = filename;
        }
        fullname = file.getPath();

        path = file.getParent() + "\\";
    }
}