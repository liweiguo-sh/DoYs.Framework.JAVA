/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-11-18
 * 集中打印服务类
 *****************************************************************************/
package com.doys.aprint.print;
import com.doys.framework.core.base.BaseService;
import com.doys.framework.database.DBFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class QuickPrintService extends BaseService {
    public static SqlRowSet getLabelVariable(DBFactory dbBus, int labelId) throws Exception {
        String sql;

        SqlRowSet rsLabelVariable;
        // ------------------------------------------------
        sql = "SELECT LOWER(name) name, type, value, rule_date_format, quote_from, rule_date_offset, rule_date_offset_unit, flag_manual_modify, js_after_input " +
            "FROM base_label_variable " +
            "WHERE label_id = ? AND type <> 'fixed' " +
            "ORDER BY sequence, name";
        rsLabelVariable = dbBus.getRowSet(sql, labelId);
        return rsLabelVariable;
    }
}