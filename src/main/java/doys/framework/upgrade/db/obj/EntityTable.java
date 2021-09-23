package doys.framework.upgrade.db.obj;
import doys.framework.core.ex.CommonException;
import doys.framework.upgrade.db.enumeration.EntityFieldType;
import doys.framework.upgrade.db.enumeration.EntityTableMatch;

import java.util.ArrayList;

public class EntityTable {
    public String databasePK = "";
    public String name = "";
    /**
     * 表中文名称
     */
    public String text = "";
    /**
     * 表说明
     */
    public String remark = "";
    public EntityTableMatch match = EntityTableMatch.appand;

    /**
     * 字段数量
     */
    public int columnCount;
    public ArrayList<EntityField> entityFields = new ArrayList<>();

    public void addFieldDefinition(EntityField entityField) {
        this.entityFields.add(entityField);
    }

    /**
     * 主键
     */
    public String pk = "";
    /**
     * 唯一索引数组
     */
    public String[] ux = {};
    /**
     * 普通索引数组
     */
    public String[] ix = {};

    /**
     * @return 返回建表SQL
     */
    public String getCreateTableSql() throws Exception {
        boolean isFirstField = true;
        String sqlReturn = "", autoColName = "";
        StringBuilder sb = new StringBuilder();

        // ------------------------------------------------
        sb.append("CREATE TABLE `" + name + "` (");
        // -- 1. 字段部分 --
        for (EntityField field : entityFields) {
            if (isFirstField) {
                isFirstField = false;
            }
            else {
                sb.append(",");
            }
            sb.append("\n\t`" + field.name + "` ");

            if (field.type == EntityFieldType.INT) {
                sb.append(field.type);
            }
            else if (field.type == EntityFieldType.TINYINT) {
                sb.append("tinyint");
            }
            else if (field.type == EntityFieldType.LONG) {
                sb.append("bigint");
            }
            else if (field.type == EntityFieldType.STRING) {
                sb.append("varchar(" + field.length + ")");
            }
            else if (field.type == EntityFieldType.FLOAT || field.type == EntityFieldType.DOUBLE) {
                sb.append(field.type + "(" + field.length + ")");
            }
            else if (field.type == EntityFieldType.DECIMAL) {
                sb.append(field.type + "(" + field.length + ")");
            }
            else if (field.type == EntityFieldType.DATETIME || field.type == EntityFieldType.DATE || field.type == EntityFieldType.TIME) {
                sb.append(field.type);
            }
            else if (field.type == EntityFieldType.TEXT) {
                sb.append("text");
            }
            else {
                throw new CommonException("待补充代码");
            }
            if (field.auto) {
                autoColName = field.name;
                sb.append(" AUTO_INCREMENT");
            }
            if (field.auto || field.not_null) {
                sb.append(" NOT NULL");
            }
            if (field.default_value != null) {
                if (field.type == EntityFieldType.INT || field.type == EntityFieldType.TINYINT || field.type == EntityFieldType.LONG) {
                    if (field.default_value.equals("")) {
                        sb.append(" DEFAULT 0");
                    }
                    else {
                        sb.append(" DEFAULT " + field.default_value);
                    }
                }
                else if (field.type == EntityFieldType.DATETIME || field.type == EntityFieldType.DATE || field.type == EntityFieldType.TIME) {
                    if (!field.default_value.equals("")) {
                        sb.append(" DEFAULT " + field.default_value);
                    }
                }
                else {
                    sb.append(" DEFAULT '" + field.default_value + "'");
                }
            }
            sb.append(" COMMENT '" + (field.text.equals("") ? field.name : field.text) + "|" + field.comment + "'");
        }

        // -- 2. 索引部分 --
        if (!autoColName.equals("")) {
            sb.append(",\n\tPRIMARY KEY (" + autoColName + ")");
        }
        else if (!pk.equals("")) {
            sb.append(",\n\tPRIMARY KEY (" + pk + ")");
        }

        for (int i = 0; i < ux.length; i++) {
            sb.append(",\r\n\tUNIQUE INDEX ux" + (i + 1) + "_" + name + "(" + ux[i] + ")");
        }
        for (int i = 0; i < ix.length; i++) {
            sb.append(",\r\n\tINDEX ix" + (i + 1) + "_" + name + "(" + ix[i] + ")");
        }

        // -- 9. end --
        sb.append("\r\n);");
        sqlReturn = sb.toString();
        return sqlReturn;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("TABLE: " + name);
        if (!text.equals("")) {
            sb.append(", text = " + text);
        }
        if (!remark.equals("")) {
            sb.append(", remark = " + remark);
        }

        sb.append("\r\nINDEXES:");
        if (!pk.equals("")) {
            sb.append("\r\n\tpk = " + pk);
        }
        if (ux.length > 0) {
            sb.append("\r\n\tux = " + ux + ";  ");
        }
        if (ix.length > 0) {
            sb.append("\r\n\tux = " + ix + ";  ");
        }

        sb.append("\r\nFIELDS:");
        if (columnCount > 0) {
            for (int i = 0; i < this.entityFields.size(); i++) {
                sb.append("\r\n\t" + entityFields.get(i).toString());
            }
        }
        else {
            sb.append("\r\n\tcolumn count is zero in physical database");
        }

        return sb.toString();
    }
}