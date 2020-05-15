package com.doys.framework.common;
import org.springframework.util.ResourceUtils;

public class UtilEnv {
    /**
     * 根据包名返回包的物理路径     *
     *
     * @param packageName 包名称，示例：com.fortec.bm.abc
     * @return 类的磁盘路径，示例：c:\xyz\com\fortec\bm\abc\
     */
    public static String getPackagePath(String packageName) throws Exception {
        String packagePath = ResourceUtils.getURL("classpath:").getPath().substring(1) + packageName.replaceAll("\\.", "/");
        if (!packagePath.endsWith("/")) {
            packagePath += "/";
        }
        return packagePath;
    }

    /**
     * 得到WebRoot物理路径
     */
    public static String getWebRootPath() throws Exception {
        return ResourceUtils.getURL("classpath:").getPath().substring(1) + "static";
    }
}