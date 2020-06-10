package com.doys.framework.core.db;
import java.util.HashMap;
public class EntityTable {
    private long id = 0;

    private String tableName;

    private HashMap<String, Object> map;

    protected DBFactory dbSys;
    // ------------------------------------------------------------------------
    public EntityTable(DBFactory dbSys, String tableName) throws Exception {
        _EntityTable(dbSys, tableName, 0);
    }
    public EntityTable(DBFactory dbSys, String tableName, long id) throws Exception {
        _EntityTable(dbSys, tableName, id);
    }
    private void _EntityTable(DBFactory dbSys, String tableName, long id) throws Exception {
        this.dbSys = dbSys;
        this.tableName = dbSys.replaceSQL(tableName);

        this.map = new HashMap<>();
    }

    // ------------------------------------------------------------------------
    public EntityTable setValue(String columnName, Object columnValue) {
        columnName = columnName.toLowerCase();
        if (this.map.containsKey(columnName)) {
            this.map.replace(columnName, columnValue);
        }
        else {
            this.map.put(columnName, columnValue);
        }
        return this;
    }
    public long getId() throws Exception {
        if (id > 0) {
            return id;
        }
        else {
            throw new Exception("记录尚未保存。");
        }
    }

    public long Save() throws Exception {
        if (this.id == 0) {
            return this.Insert();
        }
        else {
            return this.Update();
        }
    }
    private long Insert() throws Exception {
        int nColIndex = 0;
        int columnCount = this.map.size();
        long idInsert = 0;

        String sql;
        Object[] args = new Object[columnCount];

        StringBuilder builder = new StringBuilder();
        StringBuilder builderField = new StringBuilder(columnCount);
        StringBuilder builderValue = new StringBuilder(columnCount);
        // -- 1. 生成sql ------------------------------------
        for (String key : this.map.keySet()) {
            builderField.append("," + key);
            builderValue.append(",?");
            args[nColIndex++] = this.map.get(key);
        }

        builder.append("INSERT INTO " + this.tableName + " (");
        if (builderField.length() > 0) {
            builder.append(builderField.toString().substring(1));
        }
        builder.append(") VALUES (");
        if (builderValue.length() > 0) {
            builder.append(builderValue.toString().substring(1));
        }
        builder.append(")");
        sql = builder.toString();

        // -- 9. 插入数据，返回id --------------------------------
        dbSys.exec(sql, args);
        idInsert = dbSys.getLong("SELECT @@identity");

        this.id = idInsert;
        return idInsert;
    }
    private long Update() throws Exception {
        return -1;
    }
}
