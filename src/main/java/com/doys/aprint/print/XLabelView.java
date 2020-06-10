package com.doys.aprint.print;
import com.doys.framework.core.view.BaseViewController;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aprint/print/x_label_view")
public class XLabelView extends BaseViewController {
    @Override
    protected SqlRowSet getViewField(String viewPk) throws Exception {
        String sql, sql1, sql2;
        String sqlFixed, sqlDynamic;
        // ------------------------------------------------
        sql1 = "SELECT text, fixed, align, width, data_source_type, data_source, 1 id FROM sys_view_field WHERE view_pk = 'x_label' AND name = 'variable_xxx'";
        sql2 = "SELECT 1 id, LOWER(name) name, sequence FROM aprint_100.base_label_variable WHERE label_id = 4";
        sqlDynamic = "SELECT name, name text, fixed, align, width, data_source_type, data_source, sequence + 100 sequence FROM ("
            + sql1 + ") f INNER JOIN (" + sql2 + ") n ON f.id = n.id";

        // ------------------------------------------------
        sqlFixed = "SELECT  name, text, fixed, align, width, data_source_type, data_source, sequence "
            + "FROM sys_view_field "
            + "WHERE view_pk = ? AND sequence <> 0 ";

        sql = "SELECT * FROM (" + sqlFixed + " UNION " + sqlDynamic + ") t ORDER BY sequence";
        return dbSys.getRowSet(sql, viewPk);
    }
    @Override
    protected String getUseDefDataSource() {
        String sql;

        sql = "SELECT * FROM ..x_label_4";
        return sql;
    }
}