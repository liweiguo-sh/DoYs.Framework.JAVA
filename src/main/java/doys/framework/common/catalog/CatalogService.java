/******************************************************************************
 * Copyright (C), 2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2021-08-25
 * 通用目录服务类
 *****************************************************************************/
package doys.framework.common.catalog;
import doys.framework.a0.Const;
import doys.framework.a2.base.ENTITY_RECORD;
import doys.framework.core.ex.CommonException;
import doys.framework.database.DBFactory;
import doys.framework.util.UtilString;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class CatalogService {
    // -- 目录定义视图网格页面 ----------------------------------------------------------
    public static SqlRowSet getCategoryRoot(DBFactory dbBus, String CATALOG_TABLE, String nodeKeyRoot) throws Exception {
        int result;

        String sql;
        SqlRowSet rsNode;
        // -- 0. 预处理 --------------------------------------
        if (!nodeKeyRoot.equals("000")) {
            throw new CommonException("根节点必须是000，请检查。");
        }

        // -- 1. 检查缺省记录是否存在(0记录及根节点) ----------------------
        sql = "SELECT COUNT(1) FROM " + CATALOG_TABLE + " WHERE id = 0";
        result = dbBus.getInt(sql, -1);
        if (result == 0) {
            ENTITY_RECORD record = new ENTITY_RECORD(dbBus, CATALOG_TABLE);
            record
                .setValue("id_parent", 0)
                .setValue("id", 0)
                .setValue("node_key", "")
                .setValue("name", "未分类")
                .setValue("fullname", "未分类")
                .Save();
        }

        sql = "SELECT COUNT(1) FROM " + CATALOG_TABLE + " WHERE node_key = '000'";
        result = dbBus.getInt(sql, -1);
        if (result == 0) {
            ENTITY_RECORD record = new ENTITY_RECORD(dbBus, CATALOG_TABLE);
            record
                .setValue("id_parent", -1)
                .setValue("id", 1)
                .setValue("node_key", nodeKeyRoot)
                .setValue("name", "目录根节点")
                .setValue("fullname", "目录根节点")
                .setValue("is_leaf", 1)
                .Save();
        }

        // -- 2. 返回根节点记录 ----------------------------------
        sql = "SELECT node_key, id, name FROM " + CATALOG_TABLE + " WHERE node_key = ?";
        rsNode = dbBus.getRowSet(sql, nodeKeyRoot);
        return rsNode;
    }
    public static SqlRowSet getCategoryNode(DBFactory dbBus, String CATALOG_TABLE, String nodeKeyParent) throws Exception {
        int idParent;
        String sql;
        SqlRowSet rsNode;
        // ------------------------------------------------
        sql = "SELECT id FROM " + CATALOG_TABLE + " WHERE node_key = ?";
        idParent = dbBus.getInt(sql, DBFactory.NULL_NUMBER, nodeKeyParent);

        sql = "SELECT node_key, id, name, fullname, is_leaf FROM " + CATALOG_TABLE + " WHERE id_parent = ? ORDER BY sequence, name";
        rsNode = dbBus.getRowSet(sql, idParent);
        return rsNode;
    }

    // -- 目录定义视图编辑页面 ----------------------------------------------------------
    public static String getNewNodeKey(DBFactory dbBus, String CATALOG_TABLE, int idParent) throws Exception {
        String sql;
        String nodeKeyNew, nodeKeyParent, nodeKeyMaxSibling;

        sql = "SELECT node_key FROM " + CATALOG_TABLE + " WHERE id = ?";
        nodeKeyParent = dbBus.getValue(sql, "", idParent);

        sql = "SELECT MAX(node_key) FROM " + CATALOG_TABLE + " WHERE id_parent = ?";
        nodeKeyMaxSibling = dbBus.getValue(sql, "", idParent);
        if (nodeKeyMaxSibling.equalsIgnoreCase("")) {
            nodeKeyMaxSibling = "000";
        }
        else {
            nodeKeyMaxSibling = nodeKeyMaxSibling.substring(nodeKeyMaxSibling.length() - 3);
        }
        nodeKeyNew = nodeKeyParent + UtilString.getNewNodeKey(nodeKeyMaxSibling, 3);
        return nodeKeyNew;
    }
    public static boolean hasChildren(DBFactory dbBus, String CATALOG_TABLE, long id) throws Exception {
        int result;
        String sql;

        sql = "SELECT COUNT(1) FROM " + CATALOG_TABLE + " WHERE id_parent = ?";
        result = dbBus.getInt(sql, 0, id);
        if (result > 0) {
            return true;
        }
        return false;
    }
    public static void afterAddnew(DBFactory dbBus, String CATALOG_TABLE, int idParent) throws Exception {
        String sql;

        sql = "UPDATE " + CATALOG_TABLE + " SET is_leaf = 0 WHERE id = ?";
        dbBus.exec(sql, idParent);
    }
    public static void afterDelete(DBFactory dbBus, String CATALOG_TABLE, int idParent) throws Exception {
        int children;
        String sql;

        sql = "SELECT COUNT(1) children FROM " + CATALOG_TABLE + " WHERE id_parent = ?";
        children = dbBus.getInt(sql, 0, idParent);
        if (children == 0) {
            sql = "UPDATE " + CATALOG_TABLE + " SET is_leaf = 1 WHERE id = ?";
            dbBus.exec(sql, idParent);
        }
    }

    // -- idX, nameX, fullname and sequences ----------------------------------
    public static void updateX(DBFactory dbBus, String CATALOG_TABLE, String nodeKeyL1) throws Exception {
        int idL1;

        String sql;

        SqlRowSet rsParent, rsChild;
        // -- 0. 更新1级节点自身 ---------------------------------
        sql = "SELECT id FROM " + CATALOG_TABLE + " WHERE node_key = ?";
        idL1 = dbBus.getInt(sql, DBFactory.NULL_NUMBER, nodeKeyL1);

        sql = "UPDATE " + CATALOG_TABLE + " SET fullname = name, sequences = LPAD(sequence, 3 , 0), id1 = id, name1 = name WHERE id = ?";
        dbBus.exec(sql, idL1);

        // -- 1. 批量更新下级节点 ---------------------------------
        sql = "SELECT node_key, id, name, fullname, sequences FROM " + CATALOG_TABLE + " WHERE id = ?";
        rsParent = dbBus.getRowSet(sql, idL1);
        rsParent.next();

        sql = "UPDATE " + CATALOG_TABLE + " SET id1 = ?, name1 = ?, "
            + "fullname = CONCAT('" + rsParent.getString("name") + "', '" + Const.CHAR1 + "', name), "
            + "sequences = CONCAT('" + rsParent.getString("sequences") + "', '_', LPAD(sequence, 3 , 0)) "
            + "WHERE node_key <> ? AND LEFT(node_key, 6) = ?";
        dbBus.exec(sql, idL1, rsParent.getString("name"), nodeKeyL1, nodeKeyL1);

        // -- 2. 递归下级节点 -----------------------------------
        sql = "SELECT id, node_key FROM " + CATALOG_TABLE + " WHERE id_parent = ?";
        rsChild = dbBus.getRowSet(sql, idL1);
        while (rsChild.next()) {
            updateChildren(dbBus, CATALOG_TABLE, rsChild.getInt("id"), rsChild.getString("node_key").length() / 3 - 1);
        }

        // -- 3. 修改CHAR1 ----------------------------------
        sql = "UPDATE " + CATALOG_TABLE + " SET fullname = REPLACE(fullname, ?, ' \\\\ ') WHERE id1 = ?";
        dbBus.exec(sql, Const.CHAR1, idL1);
    }
    public static void updateChildren(DBFactory dbBus, String CATALOG_TABLE, int id, int level) throws Exception {
        String sql;

        SqlRowSet rsParent, rsChild;
        // -- 1. 批量更新下级节点 ---------------------------------
        sql = "SELECT node_key, id, name, fullname, sequences FROM " + CATALOG_TABLE + " WHERE id = ?";
        rsParent = dbBus.getRowSet(sql, id);
        rsParent.next();

        sql = "UPDATE " + CATALOG_TABLE + " SET id" + level + " = ?, name" + level + " = ? "
            + "WHERE LEFT(node_key, " + 3 * (level + 1) + ") = ?";
        dbBus.exec(sql, rsParent.getInt("id"), rsParent.getString("name"), rsParent.getString("node_key"));

        sql = "UPDATE " + CATALOG_TABLE + " SET "
            + "fullname = CONCAT('" + rsParent.getString("fullname") + "', '" + Const.CHAR1 + "', name), "
            + "sequences = CONCAT('" + rsParent.getString("sequences") + "', '_', LPAD(sequence, 3 , 0)) "
            + "WHERE node_key <> ? AND LEFT(node_key, " + 3 * (level + 1) + ") = ?";
        dbBus.exec(sql, rsParent.getString("node_key"), rsParent.getString("node_key"));

        // -- 2. 递归下级节点 -----------------------------------
        sql = "SELECT id, node_key FROM " + CATALOG_TABLE + " WHERE id_parent = ?";
        rsChild = dbBus.getRowSet(sql, id);
        while (rsChild.next()) {
            updateChildren(dbBus, CATALOG_TABLE, rsChild.getInt("id"), rsChild.getString("node_key").length() / 3 - 1);
        }
    }
}