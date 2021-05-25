/******************************************************************************
 * Copyright (C), 2020-2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-07-07
 * @modify_date 2021-05-25
 * Yml文件读取工具类
 *****************************************************************************/
package doys.framework.util;
import doys.framework.database.ds.UtilTDS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class UtilYml {
    @Autowired
    private Environment _environment;
    private static Environment environment;

    @Value("${upgrade-database.delete-field.keep-days:31}")                 // -- 默认值写法, 避免配置文件未配置该项出错 --
    private String _deleteFieldKeepDays;
    private static String deleteFieldKeepDays;

    @Value("${global.resRunPath}")
    private String _mResRunPath;
    private static String resRunPath;

    @Value("${global.resTempPath}")
    private String _mResTempPath;
    public static String resTempPath;

    @Value("${upgrade-database.mysql-db-backup:}")                          // -- 默认值为空的写法，可以在yml中不配置 --
    private String _mysql_db_backup;
    public static String mysql_db_backup;
    @Value("${upgrade-database.mysql-db-restore:}")
    private String _mysql_db_restore;
    public static String mysql_db_restore;
    // -- init ----------------------------------------------------------------
    @PostConstruct
    private void initYmlProperty() {
        environment = _environment;

        deleteFieldKeepDays = _deleteFieldKeepDays;

        resRunPath = _mResRunPath;
        resTempPath = _mResTempPath;

        mysql_db_backup = _mysql_db_backup;
        mysql_db_restore = _mysql_db_restore;
    }

    // -- public method 1 -----------------------------------------------------
    public static String getValue(String key) throws Exception {
        return environment.getProperty(key);
    }
    public static String getApiBase() throws Exception {
        String ip = UtilYml.getValue("server.domain");
        String port = UtilYml.getValue("server.port");
        String path = UtilYml.getValue("server.servlet.context-path");
        return "http://" + ip + ":" + port + path;
    }

    public static int getInt(String key) throws Exception {
        String strValue = getString(key);
        if (strValue.equalsIgnoreCase("")) {
            return 0;
        }
        return Integer.parseInt(strValue);
    }
    public static String getString(String key) throws Exception {
        if (key.equalsIgnoreCase("deleteFieldKeepDays")) {
            return deleteFieldKeepDays;
        }
        else {
            return "";
        }
    }

    // -- public method 2 -----------------------------------------------------
    private static String getRelativePath(String relativePath) {
        if (!relativePath.equals("")) {
            relativePath = relativePath.replaceAll("\\\\", "/");
            if (relativePath.startsWith("/")) {
                relativePath = relativePath.substring(1);
            }
            if (!relativePath.endsWith("/")) {
                relativePath += "/";
            }
        }
        return relativePath;
    }

    public static String getRunPath(String relativePath) throws Exception {
        String path = resRunPath + "/" + UtilTDS.getTenantId() + "/" + getRelativePath(relativePath);
        UtilFile.checkPath(path, true);
        return path;
    }
    public static String getRunRootPath() throws Exception {
        return resRunPath + "/";
    }
    public static String getTempPath(String relativePath) throws Exception {
        String path = resTempPath + "/" + UtilTDS.getTenantId() + "/" + getRelativePath(relativePath);
        UtilFile.checkPath(path, true);
        return path;
    }
    public static String getTempRootPath() throws Exception {
        return resTempPath + "/";
    }

    public static String getTempRootVPath() throws Exception {
        return "/resTemp/";
    }
    public static String getTempVPath(String relativePath) throws Exception {
        return "/resTemp/" + UtilTDS.getTenantId() + "/" + getRelativePath(relativePath);
    }
}