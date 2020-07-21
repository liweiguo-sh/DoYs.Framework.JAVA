package com.doys.aprint.base;
import com.doys.framework.config.Const;
import com.doys.framework.core.view.BaseViewController;
import com.doys.framework.database.DBFactory;
import com.doys.framework.util.UtilString;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aprint/base/category_view")
public class CategoryView extends BaseViewController {
    @Override
    protected boolean BeforeSave(boolean addnew, long id) throws Exception {
        int idParent = inInt("id_parent");

        String sql;
        String nodeKeyParent, nodeKeyMaxSibling, nodeKeyNew;
        // ------------------------------------------------
        if (addnew) {
            sql = "SELECT node_key FROM base_category WHERE id = ?";
            nodeKeyParent = dbBus.getValue(sql, "", idParent);

            sql = "SELECT MAX(node_key) FROM base_category WHERE id_parent = ?";
            nodeKeyMaxSibling = dbBus.getValue(sql, "", idParent);
            if (nodeKeyMaxSibling.equalsIgnoreCase("")) {
                nodeKeyMaxSibling = "000";
            }
            else {
                nodeKeyMaxSibling = nodeKeyMaxSibling.substring(nodeKeyMaxSibling.length() - 3, nodeKeyMaxSibling.length());
            }
            nodeKeyNew = nodeKeyParent + UtilString.getNewNodeKey(nodeKeyMaxSibling, 3);

            setFormValue("node_key", nodeKeyNew);
        }

        return true;
    }
    @Override
    protected boolean AfterSave(boolean addnew, long id) throws Exception {
        int idParent = inInt("id_parent");

        String sql;
        String nodeKey = in("node_key");
        // ------------------------------------------------
        if (addnew) {
            sql = "UPDATE base_category SET is_leaf = 0 WHERE id = ?";
            dbBus.exec(sql, idParent);
        }

        updateX(nodeKey.substring(0, 6));
        // ------------------------------------------------
        return true;
    }

    @Override
    protected boolean BeforeDelete(long id) throws Exception {
        int result;

        String sql;
        // ------------------------------------------------
        sql = "SELECT COUNT(1) FROM base_category WHERE id_parent = ?";
        result = dbBus.getInt(sql, 0, id);

        if (result > 0) {
            err("存在下级分类，不能删除当前分类，请检查。");
            return false;
        }
        // ------------------------------------------------
        return true;
    }
    @Override
    protected boolean AfterDelete(long id) throws Exception {
        int idParent = inInt("id_parent");
        int children;

        String sql;
        // ------------------------------------------------
        sql = "UPDATE base_label SET category_id = 0 WHERE category_id = ?";
        dbBus.exec(sql, id);

        sql = "SELECT COUNT(1) children FROM base_category WHERE id_parent = ?";
        children = dbBus.getInt(sql, 0, idParent);
        if (children == 0) {
            sql = "UPDATE base_category SET is_leaf = 1 WHERE id = ?";
            dbBus.exec(sql, idParent);
        }
        // ------------------------------------------------
        return true;
    }

    // -- idX, nameX, fullname and sequences ----------------------------------
    private void updateX(String nodeKeyL1) throws Exception {
        int idL1;

        String sql;

        SqlRowSet rsParent, rsChild;
        // -- 0. 更新1级节点自身 ---------------------------------
        sql = "SELECT id FROM base_category WHERE node_key = ?";
        idL1 = dbBus.getInt(sql, DBFactory.NULL_NUMBER, nodeKeyL1);

        sql = "UPDATE base_category SET fullname = name, sequences = sequence, id1 = id, name1 = name WHERE id = ?";
        dbBus.exec(sql, idL1);

        // -- 1. 批量更新下级节点 ---------------------------------
        sql = "SELECT node_key, id, name, fullname, sequence FROM base_category WHERE id = ?";
        rsParent = dbBus.getRowSet(sql, idL1);
        rsParent.next();

        sql = "UPDATE base_category SET id1 = ?, name1 = ?, "
            + "fullname = CONCAT('" + rsParent.getString("name") + "', '" + Const.CHAR1 + "', name), "
            + "sequences = CONCAT('" + rsParent.getString("sequence") + "', '_', sequence) "
            + "WHERE node_key <> ? AND LEFT(node_key, 6) = ?";
        dbBus.exec(sql, idL1, rsParent.getString("name"), nodeKeyL1, nodeKeyL1);

        // -- 2. 递归下级节点 -----------------------------------
        sql = "SELECT id, node_key FROM base_category WHERE id_parent = ?";
        rsChild = dbBus.getRowSet(sql, idL1);
        while (rsChild.next()) {
            updateChildren(rsChild.getInt("id"), rsChild.getString("node_key").length() / 3 - 1);
        }

        // -- 3. 修改CHAR1 ----------------------------------
        sql = "UPDATE base_category SET fullname = REPLACE(fullname, ?, ' \\\\ ') WHERE id1 = ?";
        dbBus.exec(sql, Const.CHAR1, idL1);
    }
    private void updateChildren(int id, int level) throws Exception {
        String sql;

        SqlRowSet rsParent, rsChild;
        // -- 1. 批量更新下级节点 ---------------------------------
        sql = "SELECT node_key, id, name, fullname, sequences FROM base_category WHERE id = ?";
        rsParent = dbBus.getRowSet(sql, id);
        rsParent.next();

        sql = "UPDATE base_category SET id" + level + " = ?, name" + level + " = ? "
            + "WHERE LEFT(node_key, " + 3 * (level + 1) + ") = ?";
        dbBus.exec(sql, rsParent.getInt("id"), rsParent.getString("name"), rsParent.getString("node_key"));

        sql = "UPDATE base_category SET "
            + "fullname = CONCAT('" + rsParent.getString("fullname") + "', '" + Const.CHAR1 + "', name), "
            + "sequences = CONCAT('" + rsParent.getString("sequences") + "', '_', sequence) "
            + "WHERE node_key <> ? AND LEFT(node_key, " + 3 * (level + 1) + ") = ?";
        dbBus.exec(sql, rsParent.getString("node_key"), rsParent.getString("node_key"));

        // -- 2. 递归下级节点 -----------------------------------
        sql = "SELECT id, node_key FROM base_category WHERE id_parent = ?";
        rsChild = dbBus.getRowSet(sql, id);
        while (rsChild.next()) {
            updateChildren(rsChild.getInt("id"), rsChild.getString("node_key").length() / 3 - 1);
        }
    }
}