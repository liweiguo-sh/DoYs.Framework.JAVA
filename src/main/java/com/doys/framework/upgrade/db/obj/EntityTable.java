package com.doys.framework.upgrade.db.obj;
import com.doys.framework.upgrade.db.enum1.EntityFieldType;

import java.util.ArrayList;

public class EntityTable {
    public String datbasePK = "";
    public String name = "";
    /**
     * 表中文名称
     */
    public String text = "";
    /**
     * 表说明
     */
    public String remark = "";

    /**
     * 字段数量
     */
    public int columnCount;
    public ArrayList<EntityField> entityFields = new ArrayList<>();

    public void addColumnDefinition(EntityField entityField) {
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
        try {
            sb.append("CREATE TABLE `" + name + "` (");
            // -- 1. 字段部分 --
            for (EntityField cd : entityFields) {
                if (isFirstField) {
                    isFirstField = false;
                }
                else {
                    sb.append(",");
                }
                sb.append("\n\t`" + cd.name + "` ");

                if (cd.type == EntityFieldType.INT) {
                    sb.append(cd.type);
                }
                else if (cd.type == EntityFieldType.TINYINT) {
                    sb.append("tinyint");
                }
                else if (cd.type == EntityFieldType.LONG) {
                    sb.append("bigint");
                }
                else if (cd.type == EntityFieldType.STRING) {
                    sb.append("varchar(" + cd.length + ")");
                }
                else if (cd.type == EntityFieldType.FLOAT || cd.type == EntityFieldType.DOUBLE) {
                    sb.append(cd.type + "(" + cd.length + ")");
                }
                else if (cd.type == EntityFieldType.DATETIME || cd.type == EntityFieldType.DATE || cd.type == EntityFieldType.TIME) {
                    sb.append(cd.type);
                }
                else if (cd.type == EntityFieldType.TEXT) {
                    sb.append("text");
                }
                else {
                    throw new Exception("待补充代码");
                }
                if (cd.auto) {
                    autoColName = cd.name;
                    sb.append(" AUTO_INCREMENT");
                }
                if (cd.auto || cd.not_null) {
                    sb.append(" NOT NULL");
                }
                if (!cd.default_value.equals("")) {
                    if (cd.type == EntityFieldType.INT || cd.type == EntityFieldType.TINYINT || cd.type == EntityFieldType.LONG) {
                        sb.append(" DEFAULT " + cd.default_value);
                    }
                    else if (cd.type == EntityFieldType.DATETIME || cd.type == EntityFieldType.DATE || cd.type == EntityFieldType.TIME) {
                        sb.append(" DEFAULT " + cd.default_value);
                    }
                    else {
                        if (cd.default_value.equals("''")) {
                            sb.append(" DEFAULT ''");
                        }
                        else {
                            sb.append(" DEFAULT '" + cd.default_value + "'");
                        }
                    }
                }
                sb.append(" COMMENT '" + (cd.text.equals("") ? cd.name : cd.text) + "|" + cd.comment + "'");
            }

            // -- 2. 索引部分 --
            if (!autoColName.equals("")) {
                sb.append(",\n\tPRIMARY KEY (" + autoColName + ")");
            }
            else if (!pk.equals("")) {
                sb.append(",\n\tPRIMARY KEY (" + pk + ")");
            }

            for (int i = 0; i < ux.length; i++) {
                sb.append(",\r\n\tUNIQUE INDEX UX__" + (i + 1) + "(" + ux[i] + ")");
            }
            for (int i = 0; i < ix.length; i++) {
                sb.append(",\r\n\tINDEX IX__" + (i + 1) + "(" + ix[i] + ")");
            }

            // -- 9. end --
            sb.append("\r\n);");
            sqlReturn = sb.toString();
        } catch (Exception e) {
            throw e;
        }
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