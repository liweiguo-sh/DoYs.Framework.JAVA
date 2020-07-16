package com.doys.aprint.base;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aprint/base/category")
public class CategoryController extends BaseController {
    @RequestMapping("/getCategoryNode")
    private RestResult getCategoryNode() {
        int level = inInt("level");
        int idParent = 0;

        String sql;
        SqlRowSet rsNode;
        // ------------------------------------------------
        try {
            if (level == 0) {
                sql = "SELECT id_parent FROM base_category WHERE node_key = '000'";
                idParent = dbBus.getInt(sql, -9);
                if (idParent == -9) {
                    CategoryService.initCategory(dbBus);
                    idParent = dbBus.getInt(sql);
                }
            }
            else {
                idParent = inInt("idParent");
            }

            sql = "SELECT id, node_key, name, is_leaf FROM base_category WHERE id_parent = ?";
            rsNode = dbBus.getRowSet(sql, idParent);
            ok("dtbNode", rsNode);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}