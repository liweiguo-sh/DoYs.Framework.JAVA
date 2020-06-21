package com.doys.framework.upgrade.service;
import com.doys.framework.common.io.ScanFile;
import com.doys.framework.config.DataSourceDelegating;
import com.doys.framework.core.base.BaseService;
import com.doys.framework.core.db.DBFactory;
import com.doys.framework.core.ex.UnImplementException;
import com.doys.framework.upgrade.db.annotation.EntityClassAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.enum1.EntityFieldType;
import com.doys.framework.upgrade.db.enum1.EntityIndexType;
import com.doys.framework.upgrade.db.obj.EntityClass;
import com.doys.framework.upgrade.db.obj.EntityField;
import com.doys.framework.upgrade.db.util.ClassReflect;
import com.doys.framework.upgrade.db.util.MySqlSysHelper;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class UpgradeDatabaseService extends BaseService {
    public static void upgrade(DBFactory dbSys, DataSource busDS, String entityPaths) throws Exception {
        String classFile = "";
        ArrayList<EntityClass> entityClasses = new ArrayList<>();
        try {
            // -- 扫描实体类 --
            ArrayList<String> alClassFile = ScanFile.scanClass(entityPaths, true);
            for (int i = 0; i < alClassFile.size(); i++) {
                classFile = alClassFile.get(i);
                if (classFile.indexOf("Student") <= 0) {
                    // continue;   // -- todo: 调试用 --
                }

                Class clazz = Thread.currentThread().getContextClassLoader().loadClass(classFile);
                try {
                    Object entity = clazz.getDeclaredConstructor().newInstance();

                    EntityClass entityClass = parseTable(entity);
                    if (entityClass != null) {
                        entityClasses.add(entityClass);
                        System.err.println("======== 实体类 ======== " + classFile);
                    }
                    else {
                        System.out.println("====== 不是实体类 ====== " + classFile);
                    }
                } catch (InstantiationException e) {
                    logger.error("不是普通类，不能实例化。" + classFile);
                    continue;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("出错了" + classFile);
                }
            }

            // -- 升级数据库 --
            for (EntityClass entityClass : entityClasses) {
                //System.out.println("\n" + entityClass.toString());
                //System.out.println("\nDROP TABLE IF EXISTS " + entityClass.name + ";");
                //System.out.println(entityClass.getCreateTableSql());
                //System.out.println("\nSELECT * FROM " + entityClass.name + ";");

                upgradeTable(dbSys, busDS, entityClass);
            }
            logger.info("upgrade database success, total entity class: " + entityClasses.size());
        } catch (Exception e) {
            throw e;
        }
    }
    private static EntityClass parseTable(Object entity) throws Exception {
        EntityClass table = new EntityClass();
        EntityClassAnnotation tableProperty = entity.getClass().getAnnotation(EntityClassAnnotation.class);
        // -- 1. 判断是否实体类 --
        if (tableProperty == null) {
            // -- 不是实体类 --
            return null;
        }
        // -- 2. parse table --
        table.datbasePK = tableProperty.databasePk();
        if (!tableProperty.name().equals("")) {
            table.name = tableProperty.name();
        }
        if (!tableProperty.text().equals("")) {
            table.text = tableProperty.text();
        }
        if (!tableProperty.remark().equals("")) {
            table.remark = tableProperty.remark();
        }
        if (table.name.equals("")) {
            table.name = entity.getClass().getName().substring(entity.getClass().getPackageName().length() + 1);
        }
        // -- 3. parse index --
        EntityIndexAnnotation indexProperty = entity.getClass().getAnnotation(EntityIndexAnnotation.class);
        if (indexProperty != null) {
            table.pk = indexProperty.pk();
            table.ux = indexProperty.ux();
            table.ix = indexProperty.ix();
        }

        // -- 4. parse fields --
        List<Field> arrField = ClassReflect.getClassAllFields(entity);
        for (int i = 0; i < arrField.size(); i++) {
            EntityField entityField = parseField(arrField.get(i));
            if (entityField != null) {
                table.columnCount++;
                table.addColumnDefinition(entityField);
                if (entityField.auto) {
                    table.pk = entityField.name;
                }
            }
        }

        return table;
    }
    private static EntityField parseField(Field field) throws Exception {
        EntityField cd = new EntityField();
        cd.name = field.getName();
        cd.type = cd.parseType(field.getType().getName());

        // -- CPAS注解分析 --
        EntityFieldAnnotation efAnnotation = field.getAnnotation(EntityFieldAnnotation.class);
        if (efAnnotation != null) {
            if (efAnnotation.auto()) {
                cd.auto = true;
            }
            if (efAnnotation.type() != EntityFieldType.UNKNOWN) {
                cd.type = efAnnotation.type();
            }
            if (!efAnnotation.default_value().equals("")) {
                cd.default_value = efAnnotation.default_value();
            }
            cd.not_null = efAnnotation.not_null();
            cd.length = efAnnotation.length();
            cd.text = efAnnotation.text();
            cd.comment = efAnnotation.comment();
        }

        // -- 属性默认值 --
        if (cd.length.equals("")) {
            if (cd.type == EntityFieldType.STRING) {
                cd.length = "50";
            }
        }
        if (cd.auto) {
            cd.not_null = true;
        }
        // -- end --
        return cd;
    }

    private static void upgradeTable(DBFactory dbSys, DataSource busDataSource, EntityClass entityClass) throws Exception {
        int result;

        String sql;
        String databasePk = entityClass.datbasePK;
        String databaseName, dbNamePrefix;
        String tableName = entityClass.name;

        SqlRowSet rsTenant;
        // ------------------------------------------------
        sql = "SELECT name FROM sys_database WHERE pk = ?";
        databaseName = dbSys.getValue(sql, databasePk);

        if (databasePk.equalsIgnoreCase("sys")) {
            sql = "SELECT COUNT(1) FROM information_schema.TABLES WHERE table_schema = ? table_type = 'BASE TABLE' AND table_name = ?";
            result = dbSys.getInt(sql, databaseName, tableName);
            if (result == 0) {
                // -- 1. 表不存在：创建 --
                sql = entityClass.getCreateTableSql();
                dbSys.exec(sql);
            }
            else {
                // -- 2. 表存在：比对字段、比对索引 --
                upgradeField(dbSys, dbSys, entityClass, databaseName);
                upgradeIndex(dbSys, entityClass);
            }
        }
        else if (databasePk.equalsIgnoreCase("prefix")) {
            sql = "SELECT id FROM sys_tenant ORDER BY id";
            rsTenant = dbSys.getRowSet(sql);
            while (rsTenant.next()) {
                dbNamePrefix = databaseName + rsTenant.getInt("id");

                DBFactory dbBus = new DBFactory(new DataSourceDelegating(busDataSource, dbNamePrefix));
                String strCategory = dbBus.getDataSource().getConnection().getCatalog();

                sql = "SELECT COUNT(1) FROM information_schema.TABLES WHERE table_schema = ? AND table_type = 'BASE TABLE' AND table_name = ?";
                result = dbBus.getInt(sql, dbNamePrefix, tableName);
                if (result == 0) {
                    // -- 1. 表不存在：创建 --
                    sql = entityClass.getCreateTableSql();
                    dbBus.exec(sql);
                }
                else {
                    // -- 2. 表存在：比对字段、比对索引 --
                    upgradeField(dbSys, dbBus, entityClass, dbNamePrefix);
                    upgradeIndex(dbSys, entityClass);
                }
            }
        }
        else {
            throw new UnImplementException();
        }
    }
    private static void upgradeField(DBFactory dbSys, DBFactory dbBus, EntityClass entityClass, String databaseName) throws Exception {
        boolean blInvalidColumn;
        String columnName = "";
        String sql = "SELECT * FROM information_schema.COLUMNS WHERE table_schema = ? AND table_name = ?";
        SqlRowSet rsColumn = dbSys.getRowSet(sql, databaseName, entityClass.name);
        for (EntityField entityField : entityClass.entityFields) {
            boolean isNewColumn = true;
            rsColumn.beforeFirst();

            while ((rsColumn.next())) {
                if (rsColumn.getString("column_name").equalsIgnoreCase(entityField.name)) {
                    isNewColumn = false;
                    break;
                }
            }

            if (isNewColumn) {
                // -- 1. 列不存在，创建新列 --
                sql = "alter table " + entityClass.name + " add " + entityField.name + " " + entityField.getCreateColumnSql(true);
                System.err.println("新增字段：" + sql);
                dbBus.exec(sql);
            }
            else {
                // -- 2. 列存在，比对列属性 --
                if (checkColumnNeedUpgrade(entityField, rsColumn.getString("data_type"),
                    rsColumn.getString("character_maximum_length"), rsColumn.getInt("numeric_precision"), rsColumn.getInt("numeric_scale"),
                    rsColumn.getString("is_nullable").equalsIgnoreCase("NO"), rsColumn.getString("column_default"), rsColumn.getString("column_comment"))) {
                    sql = "alter table " + entityClass.name + " MODIFY COLUMN " + entityField.name + " " + entityField.getCreateColumnSql(false);
                    System.err.println("升级字段：" + sql);
                    dbBus.exec(sql);
                }
            }
        }

        // -- 删除列 --
        rsColumn.beforeFirst();
        while (rsColumn.next()) {
            blInvalidColumn = true;
            columnName = rsColumn.getString("column_name");
            for (EntityField entityField : entityClass.entityFields) {
                if (entityField.name.equalsIgnoreCase(columnName)) {
                    blInvalidColumn = false;
                    break;
                }
            }
            if (blInvalidColumn) {
                //MySqlSysHelper.dropColumn(dbBus, databaseName, entityClass.name, columnName);
                MySqlSysHelper.disableColumn(dbBus, databaseName, entityClass.name, columnName, rsColumn.getString("column_type"), rsColumn.getString("column_comment"));
            }
        }
    }
    /**
     * 对比是否需要升级
     *
     * @return true：表示需要升级，false：表示不需要升级
     * @throws Exception 表示Column与Field的定义冲突，必须升级失败
     */
    private static boolean checkColumnNeedUpgrade(EntityField entityField, String columnType, String columnLength, int columnPrecision, int columnScale, boolean columnNotNull, String columnDefault, String columnComment) throws Exception {
        boolean needUpgrage = true;
        String entityColumnType = entityField.getColumnType();

        // -- 0. 预处理 --
        if (columnDefault == null) {
            columnDefault = "";
        }
        else if (columnDefault.equals("")) {
            columnDefault = "''";
        }

        // -- 1. 判断是否有不同，有则需要升级 --
        for (int i = 0; i < 1; i++) {
            if (!entityColumnType.equalsIgnoreCase(columnType)) {
                break;          // -- 1.1 数据类型不同 --
            }
            else {
                if (columnType.equals("float") || columnType.equals("double")) {
                    if (!(columnPrecision + "," + columnScale).equals(entityField.length)) {
                        break;  // -- 1.2 数字精度不同 --
                    }
                }
                else if (columnType.equals("varchar")) {
                    if (!columnLength.equals(entityField.length)) {
                        break;  // -- 1.3 字符长度不同 --
                    }
                }
            }

            if (columnNotNull != entityField.not_null) {
                break;          // -- 1.4 是否为空 --
            }
            if (!columnDefault.equals(entityField.default_value)) {
                break;          // -- 1.5 默认值不同 --
            }

            String commentNew = "";
            if (!entityField.text.equals("") && !entityField.comment.equals("")) {
                commentNew = entityField.text + "|" + entityField.comment;
            }
            else if (!entityField.text.equals("")) {
                commentNew = entityField.text;
            }
            else if (!entityField.comment.equals("")) {
                commentNew = entityField.name + "|" + entityField.comment;
            }
            if (!columnComment.equals(commentNew)) {
                break;          // -- 1.6 备注不同 --
            }

            needUpgrage = false;
        }
        if (!needUpgrage) {
            return false;
        }

        // -- 2. 判断类型变化是否被允许 --
        if (!entityColumnType.equalsIgnoreCase(columnType)) {
            if ((columnType.equals("datetime") || columnType.equals("date") || columnType.equals("time"))
                && (entityField.type != EntityFieldType.DATETIME && entityField.type != EntityFieldType.DATE && entityField.type != EntityFieldType.TIME)) {
                // -- 2.1 日期转为其它类型 --
                throw new Exception("不允许从类型 " + columnType + " 转换为类型 " + entityField.type);
            }
            if ((entityField.type == EntityFieldType.DATETIME || entityField.type == EntityFieldType.DATE || entityField.type == EntityFieldType.TIME)
                && (!columnType.equals("datetime") && !columnType.equals("date") && !columnType.equals("time"))) {
                // -- 2.2 其它转为日期类型 --
                throw new Exception("不允许从类型 " + columnType + " 转换为类型 " + entityField.type);
            }

            // todo: 有数据情况尚未验证，存在数据，转换是否成功或损失精度？长度等等？
        }

        // -- 3. --

        return needUpgrage;
    }
    private static void upgradeIndex(DBFactory dbSys, EntityClass entityClass) throws Exception {
        String indexFields = "";
        ArrayList<String[]> alIndex;

        // -- 1. 主键 --
        indexFields = MySqlSysHelper.getPrimaryKey(dbSys, entityClass.name);
        if (!entityClass.pk.equals(indexFields)) {
            if (!indexFields.equals("")) {
                MySqlSysHelper.dropIndex(dbSys, entityClass.name, EntityIndexType.PRIMARY, "PRIMARY");
            }
            if (!entityClass.pk.equals("")) {
                MySqlSysHelper.addIndex(dbSys, entityClass.name, EntityIndexType.PRIMARY, "PRIMARY", entityClass.pk);
            }
        }

        // -- 2. 唯一索引 --
        alIndex = MySqlSysHelper.getIndex(dbSys, entityClass.name, EntityIndexType.UNIQUE_INDEX);
        for (int i = entityClass.ux.length; i < alIndex.size(); i++) {
            MySqlSysHelper.dropIndex(dbSys, entityClass.name, EntityIndexType.UNIQUE_INDEX, alIndex.get(i)[0]);
        }
        for (int i = 0; i < entityClass.ux.length; i++) {
            if (i < alIndex.size()) {
                if (!entityClass.ux[i].equals(alIndex.get(i)[1])) {
                    MySqlSysHelper.dropIndex(dbSys, entityClass.name, EntityIndexType.UNIQUE_INDEX, alIndex.get(i)[0]);
                    MySqlSysHelper.addIndex(dbSys, entityClass.name, EntityIndexType.UNIQUE_INDEX, "UX__" + (i + 1), entityClass.ux[i]);
                }
            }
            else {
                MySqlSysHelper.addIndex(dbSys, entityClass.name, EntityIndexType.UNIQUE_INDEX, "UX__" + (i + 1), entityClass.ux[i]);
            }
        }

        // -- 3. 普通索引 --
        alIndex = MySqlSysHelper.getIndex(dbSys, entityClass.name, EntityIndexType.INDEX);
        for (int i = entityClass.ix.length; i < alIndex.size(); i++) {
            MySqlSysHelper.dropIndex(dbSys, entityClass.name, EntityIndexType.INDEX, alIndex.get(i)[0]);
        }
        for (int i = 0; i < entityClass.ix.length; i++) {
            if (i < alIndex.size()) {
                if (!entityClass.ix[i].equals(alIndex.get(i)[1])) {
                    MySqlSysHelper.dropIndex(dbSys, entityClass.name, EntityIndexType.INDEX, alIndex.get(i)[0]);
                    MySqlSysHelper.addIndex(dbSys, entityClass.name, EntityIndexType.INDEX, "IX__" + (i + 1), entityClass.ix[i]);
                }
            }
            else {
                MySqlSysHelper.addIndex(dbSys, entityClass.name, EntityIndexType.INDEX, "IX__" + (i + 1), entityClass.ix[i]);
            }
        }
    }
}