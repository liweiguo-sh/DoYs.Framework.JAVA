package com.doys.aprint.base;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import com.doys.framework.database.DBFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aprint/base/category")
public class CategoryController extends BaseController {
    @RequestMapping("/getCategoryNode")
    private RestResult getCategoryNode() {
        int idParent;

        String sql;
        String nodeKeyParent = in("nodeKeyParent");

        SqlRowSet rsNode;
        // ------------------------------------------------
        try {
            sql = "SELECT id FROM base_category WHERE node_key = ?";
            idParent = dbBus.getInt(sql, DBFactory.NULL_NUMBER, nodeKeyParent);

            sql = "SELECT node_key, id, name, is_leaf FROM base_category WHERE id_parent = ? ORDER BY sequence, name";
            rsNode = dbBus.getRowSet(sql, idParent);
            ok("dtbNode", rsNode);

            ok("nodeKeyParent", nodeKeyParent);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
    @RequestMapping("/getCategoryRoot")
    private RestResult getCategoryRoot() {
        String sql;
        String nodeKeyRoot = "000";

        SqlRowSet rsNode;
        // ------------------------------------------------
        try {
            sql = "SELECT node_key, id, name FROM base_category WHERE node_key = ?";
            rsNode = dbBus.getRowSet(sql, nodeKeyRoot);
            ok("dtbNode", rsNode);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}