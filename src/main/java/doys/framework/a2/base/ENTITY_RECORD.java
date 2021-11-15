package doys.framework.a2.base;

import doys.framework.core.ex.CommonException;
import doys.framework.database.DBFactory;

import java.util.HashMap;
import java.util.Map;

public class ENTITY_RECORD {
    private String id = "";
    private String tableName;

    private HashMap<String, Object> map;
    protected DBFactory dbBus;
    // ------------------------------------------------------------------------
    public ENTITY_RECORD(DBFactory dbBus, String tableName) throws Exception {
        _ENTITY_RECORD(dbBus, tableName, "");
    }
    public ENTITY_RECORD(DBFactory dbBus, String tableName, String id) throws Exception {
        _ENTITY_RECORD(dbBus, tableName, id);
    }
    public ENTITY_RECORD(DBFactory dbBus, String tableName, long id) throws Exception {
        _ENTITY_RECORD(dbBus, tableName, String.valueOf(id));
    }
    private void _ENTITY_RECORD(DBFactory dbBus, String tableName, String id) throws Exception {
        this.dbBus = dbBus;
        this.tableName = tableName;
        this.id = id;

        this.map = new HashMap<>();
    }

    // ------------------------------------------------------------------------
    public ENTITY_RECORD setValue(String columnName, Object columnValue) {
        columnName = columnName.toLowerCase();
        if (this.map.containsKey(columnName)) {
            this.map.replace(columnName, columnValue);
        }
        else {
            this.map.put(columnName, columnValue);
        }
        return this;
    }
    public String getId() throws Exception {
        if (!id.equals("")) {
            return id;
        }
        else {
            throw new CommonException("记录尚未保存。");
        }
    }
    public int getIdInt() throws Exception {
        return Integer.parseInt(getId());
    }
    public long getIdLong() throws Exception {
        return Long.parseLong(getId());
    }

    // ------------------------------------------------------------------------
    public String Save() throws Exception {
        if (this.id.equals("")) {
            return this.Insert();
        }
        else {
            return this.Update();
        }
    }
    private String Insert() throws Exception {
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
        dbBus.exec(sql, args);
        if (this.id.equals("")) {
            idInsert = dbBus.getLong("SELECT @@identity");
            this.id = String.valueOf(idInsert);
        }

        return this.id;
    }
    private String Update() throws Exception {
        int nColIndex = 0;
        int columnCount = this.map.size();

        String sql, key, idNew = "";
        Object[] args = new Object[columnCount + 1];

        StringBuilder builder = new StringBuilder();
        // -- 1. 生成sql ------------------------------------
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            key = entry.getKey();
            if (key.equalsIgnoreCase("id")) {
                idNew = (String) entry.getValue();
            }
            builder.append(", " + entry.getKey() + " = ?");
            args[nColIndex++] = entry.getValue();
        }
        builder.append(" WHERE id = ?");
        args[nColIndex++] = this.id;

        sql = "UPDATE " + this.tableName + " SET " + builder.toString().substring(1);
        // -- 9. 更新数据，返回id --------------------------------
        dbBus.exec(sql, args);
        if (!idNew.equals("")) {
            this.id = idNew;
        }
        return this.id;
    }
}