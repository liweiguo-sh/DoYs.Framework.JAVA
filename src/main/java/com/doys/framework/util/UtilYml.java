/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-07-07
 * @modify_date 2020-08-18
 * Yml文件读取工具类
 *****************************************************************************/
package com.doys.framework.util;
import com.doys.framework.database.ds.UtilDDS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class UtilYml {
    @Value("${upgrade-database.delete-field.keep-days:31}")                 // -- 默认值写法, 避免配置文件未配置该项出错 --
    private String _deleteFieldKeepDays;
    private static String deleteFieldKeepDays;

    @Value("${global.resRunPath}")
    private String _mResRunPath;
    private static String resRunPath;

    @Value("${global.resTempPath}")
    private String _mResTempPath;
    public static String resTempPath;
    // -- init ----------------------------------------------------------------
    @PostConstruct
    private void initYmlProperty() {
        deleteFieldKeepDays = _deleteFieldKeepDays;

        resRunPath = _mResRunPath;
        resTempPath = _mResTempPath;
    }

    // -- public method 1 -----------------------------------------------------
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
    public static String getRunPath() throws Exception {
        return resRunPath + "/" + UtilDDS.getTenantId();
    }
    public static String getTempPath() throws Exception {
        return resTempPath + "/" + UtilDDS.getTenantId();
    }
}