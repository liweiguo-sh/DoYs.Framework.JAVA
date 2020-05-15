package com.doys.framework.upgrade.db.obj;

import com.doys.framework.upgrade.db.enum1.EntityFieldType;
import com.doys.framework.upgrade.db.util.DataTypeConvert;

public class EntityField {
    /**
     * 字段名称
     */
    public String name = "";
    /**
     * 字段类型
     */
    public EntityFieldType type = EntityFieldType.UNKNOWN;
    /**
     * 是否自增量字段
     */
    public boolean auto = false;
    public String length = "";
    /**
     * 不允许为空
     */
    public boolean not_null = false;
    /**
     * 字段默认值default(x)
     */
    public String default_value = "";
    /**
     * 字段中文名称
     */
    public String text = "";
    /**
     * 字段注释
     */
    public String comment = "";

    /**
     * @return 根据Field类型返回Column类型
     */
    public String getColumnType() throws Exception {
        return DataTypeConvert.getColumnType(type);
    }

    public EntityFieldType parseType(String type) throws Exception {
        EntityFieldType columnType = EntityFieldType.UNKNOWN;

        type = type.toLowerCase();
        if (type.equals("java.lang.string")) {
            columnType = EntityFieldType.STRING;
        } else if (type.equals("boolean") || type.equalsIgnoreCase("java.lang.boolean")) {
            columnType = EntityFieldType.TINYINT;
        } else if (type.equals("int") || type.equals("java.lang.integer")) {
            columnType = EntityFieldType.INT;
        } else if (type.equals("long") || type.equals("java.lang.long")) {
            columnType = EntityFieldType.LONG;
        } else if (type.equals("float") || type.equals("java.lang.float")) {
            columnType = EntityFieldType.FLOAT;
        } else if (type.equals("double") || type.equals("java.lang.double")) {
            columnType = EntityFieldType.DOUBLE;
        } else if (type.equals("java.time.localdatetime") || type.equals("java.util.date") || type.equals("java.sql.timestamp")) {
            columnType = EntityFieldType.DATETIME;
        } else if (type.equals("java.sql.date")) {
            columnType = EntityFieldType.DATE;
        } else if (type.equals("java.sql.time")) {
            columnType = EntityFieldType.TIME;
        } else if (type.equals("date")) {
            columnType = EntityFieldType.DATE;
        } else {
            throw new Exception("unknown columnType: " + type + ", please check it.");
        }

        return columnType;
    }

    /**
     * @return 返回添加字段sql, alter table add 后面的部分
     */
    public String getCreateColumnSql(boolean addMode) throws Exception {
        String sql = "";
        StringBuilder sb = new StringBuilder();

        if (type == EntityFieldType.STRING) {
            sb.append("varchar(" + length + ")");
        } else if (type == EntityFieldType.INT) {
            sb.append("int");
        } else if (type == EntityFieldType.TINYINT) {
            sb.append("tinyint");
        } else if (type == EntityFieldType.LONG) {
            sb.append("bigint");
        } else if (type == EntityFieldType.FLOAT || type == EntityFieldType.DOUBLE) {
            sb.append(type + " (" + length + ")");
        } else if (type == EntityFieldType.DATETIME || type == EntityFieldType.DATE || type == EntityFieldType.TIME) {
            sb.append(type);
        } else if (type == EntityFieldType.TEXT) {
            sb.append("text");
        } else {
            throw new Exception("debug here: getAddColumnSql 1");
        }

        if (not_null || auto) {
            sb.append(" NOT NULL");
        }
        if (default_value != null && !default_value.equals("")) {
            if (type == EntityFieldType.STRING) {
                if (default_value.equals("''")) {
                    sb.append(" DEFAULT ''");
                } else {
                    sb.append(" DEFAULT '" + default_value + "'");
                }
            } else if (type == EntityFieldType.INT || type == EntityFieldType.LONG || type == EntityFieldType.TINYINT) {
                sb.append(" DEFAULT " + default_value);
            } else if (type == EntityFieldType.DATETIME || type == EntityFieldType.DATE || type == EntityFieldType.TIME) {
                sb.append(" DEFAULT " + default_value);
            } else if (type == EntityFieldType.TEXT) {
                // BLOB, TEXT, GEOMETRY or JSON column  can't have a default value
                // do nothing
            } else {
                throw new Exception("debug here: getAddColumnSql 2");
            }
        }

        if (!text.equals("") && !comment.equals("")) {
            sb.append(" COMMENT '" + text + "|" + comment + "'");
        } else if (!text.equals("")) {
            sb.append(" COMMENT '" + text + "'");
        } else if (!comment.equals("")) {
            sb.append(" COMMENT '" + name + "|" + comment + "'");
        } else {
            sb.append(" COMMENT ''");
        }

        if (auto && (type == EntityFieldType.INT || type == EntityFieldType.LONG)) {
            if (addMode) {
                sb.append(" AUTO_INCREMENT, ADD PRIMARY KEY(" + name + ")");
            } else {
                sb.append(" AUTO_INCREMENT");
            }
        }

        sql = sb.toString();
        return sql;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(name);
        if (auto) {
            sb.append("(AUTO)");
        }
        sb.append(", type = " + type.name());
        if (text != "") {
            sb.append(", text = " + text);
        }
        if (type == EntityFieldType.STRING) {
            sb.append(", length = " + length);
        }
        if (not_null) {
            sb.append(", NOT NULL");
        }
        if (default_value != "") {
            sb.append(", default = " + default_value);
        }
        if (comment != "") {
            sb.append(", comment = " + comment);
        }

        return sb.toString();
    }
}