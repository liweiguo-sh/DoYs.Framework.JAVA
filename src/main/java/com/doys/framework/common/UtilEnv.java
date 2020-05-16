package com.doys.framework.common;
import org.springframework.util.ResourceUtils;

public class UtilEnv {
    /**
     * 得到WebRoot物理路径
     */
    public static String getWebRootPath() {
        try {
            return ResourceUtils.getURL("classpath:").getPath().substring(1) + "static";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据包名返回包的物理路径
     *
     * @param packageName 包名称，示例：com.fortec.bm.abc
     * @return 类的磁盘路径，示例：c:\xyz\com\fortec\bm\abc\
     */
    public static String getPackagePath(String packageName) {
        try {
            String packagePath = ResourceUtils.getURL("classpath:").getPath().substring(1) + packageName.replaceAll("\\.", "/");
            if (!packagePath.endsWith("/")) {
                packagePath += "/";
            }
            return packagePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}