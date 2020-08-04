package com.doys.framework.dts.base;
import com.doys.framework.core.ex.UnImplementException;
import com.doys.framework.database.DBFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class BASE_ENTITY {
    protected boolean isNewRecord = true;           // -- 是否新纪录 --
    protected boolean isAuto = false;               // -- 是否有自增量字段(强制约定为id) --

    private long id = 0;
    private String tableName;

    protected DBFactory dbBus;

    // -- init ----------------------------------------------------------------
    public void init(DBFactory dbBus) {
        this.dbBus = dbBus;

        String className = this.getClass().getName();
        int idx = className.lastIndexOf(".");
        this.tableName = className.substring(idx + 1);
    }
    private Exception getUnInitException() {
        return new Exception("请先执行init方法。");
    }

    // -- save (insert & update) ----------------------------------------------
    public long save() throws Exception {
        if (this.dbBus == null) {
            throw getUnInitException();
        }

        if (this.isNewRecord) {
            return this.insert();
        }
        else {
            return this.update();
        }
    }

    private long insert() throws Exception {
        int nColIndex = 0;

        HashMap<String, Field> mapFields = getFields();
        int columnCount = mapFields.size();

        String sql, fieldName, fieldValue;
        ArrayList<String> args = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        StringBuilder builderField = new StringBuilder(columnCount);
        StringBuilder builderValue = new StringBuilder(columnCount);

        // -- 1. 生成sql ------------------------------------
        for (Field field : mapFields.values()) {
            fieldName = field.getName();
            if (fieldName.equalsIgnoreCase("id") && isAuto) {
                continue;
            }

            builderField.append("," + fieldName);
            builderValue.append(",?");

            fieldValue = getFieldValue(field);
            args.add(fieldValue);
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
        dbBus.exec(sql, args.toArray());
        if (isAuto) {
            this.id = this.getId();
        }
        return this.id;
    }
    protected long getId() throws Exception {
        throw new Exception("id field does not exist");
    }

    public long update() throws Exception {
        int nColIndex = 0;

        HashMap<String, Field> mapFields = getFields();
        int columnCount = mapFields.size();

        String sql;
        Object[] args = new Object[columnCount];

        StringBuilder builder = new StringBuilder();
        StringBuilder builderField = new StringBuilder(columnCount);
        StringBuilder builderValue = new StringBuilder(columnCount);

        // -- 1. 生成sql ------------------------------------
        builder.append("UPDATE " + this.tableName + " SET ");
        for (Field field : mapFields.values()) {
            if (nColIndex > 0) {
                builder.append(",");
            }
            builder.append(field.getName() + " = ?");
            args[nColIndex++] = getFieldValue(field);
        }
        if (isAuto) {
            builder.append(" WHERE id = " + this.id);
        }
        else {
            throw new UnImplementException();
        }
        sql = builder.toString();

        // -- 9. 插入数据，返回id --------------------------------
        dbBus.exec(sql, args);
        return this.id;
    }

    // -- aux -----------------------------------------------------------------
    private HashMap<String, Field> getFields() {
        String fieldName;
        Field[] fields;
        HashMap<String, Field> map = new HashMap<>();
        // ------------------------------------------------
        fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            fieldName = field.getName();
            map.put(fieldName, field);
        }

        fields = this.getClass().getFields();
        for (Field field : fields) {
            fieldName = field.getName();
            if (!map.containsKey(fieldName)) {
                map.put(fieldName, field);
            }
        }
        // ------------------------------------------------
        return map;
    }
    private String getFieldValue(Field field) throws Exception {
        Object obj;

        field.setAccessible(true);
        obj = field.get(this);
        if (obj == null) {
            return null;
        }
        else {
            return obj.toString();
        }
    }
}