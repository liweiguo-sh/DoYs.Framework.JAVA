package com.doys.aprint.base;
import com.doys.framework.core.base.BaseService;
import com.doys.framework.database.DBFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;
public class LabelService extends BaseService {
    /**
     * 初始化标签数据表
     * 根据标签变量定义，动态生成标签数据表
     *
     * @param dbSys
     * @param labelId
     */
    public static void generateLabelXTable(DBFactory dbSys, DBFactory dbBus, int labelId) throws Exception {
        int columnLength;

        String sql;
        String dbPrefix, tableName, columnName;

        StringBuilder builder = new StringBuilder();
        SqlRowSet rs;
        // ------------------------------------------------
        dbPrefix = dbSys.getTenantDbName();
        tableName = dbPrefix + ".x_label_" + labelId;
        builder.append("CREATE TABLE " + tableName + " (");
        builder.append("\n\ttask_id int NOT NULL,");
        builder.append("\n\tid int NOT NULL AUTO_INCREMENT,");
        builder.append("\n\trow_no int NOT NULL,");

        sql = "SELECT * FROM base_label_variable WHERE label_id = ?";
        rs = dbBus.getRowSet(sql, labelId);
        while (rs.next()) {
            columnName = rs.getString("name");
            columnLength = rs.getInt("value_len");
            builder.append("\n\t" + columnName + " varchar(" + columnLength + ") NULL DEFAULT '',");
        }
        builder.append("\n\tcdate datetime NULL DEFAULT CURRENT_TIMESTAMP(0),");
        builder.append("\n\tPRIMARY KEY (id),");
        builder.append("\n\tINDEX fx_x_label_" + labelId + " (task_id)");
        builder.append("\n);");

        sql = "DROP TABLE IF EXISTS " + tableName;
        dbSys.exec(sql);

        sql = builder.toString();
        dbSys.exec(sql);
    }
}