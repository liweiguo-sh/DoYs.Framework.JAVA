/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-07-07
 * Yml文件读取工具类
 *****************************************************************************/
package com.doys.framework.util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
@Component
public class UtilYml {
    @Value("${upgrade-database.delete-field.keep-days:31}")                 // -- 默认值写法, 避免配置文件未配置该项出错 --
    private String _deleteFieldKeepDays;


    private static String deleteFieldKeepDays;
    // -- init ----------------------------------------------------------------
    @PostConstruct
    private void initDynamicDataSourceProperty() {
        deleteFieldKeepDays = _deleteFieldKeepDays;
    }

    // -- DataSource ----------------------------------------------------------
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
}