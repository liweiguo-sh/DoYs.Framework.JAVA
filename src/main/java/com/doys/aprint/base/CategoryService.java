package com.doys.aprint.base;
import com.doys.aprint.dts.base.base_category;
import com.doys.framework.core.ex.UnexpectedException;
import com.doys.framework.database.DBFactory;

public class CategoryService {
    public static void initCategory(DBFactory dbBus) throws Exception {
        int result;

        String sql;
        // ------------------------------------------------
        sql = "SELECT COUNT(1) FROM base_category";
        result = dbBus.getInt(sql);
        if (result > 0) {
            throw new UnexpectedException();
        }

        base_category category = new base_category();
        category.init(dbBus);
        category.id_parent = -1;
        category.node_key = "000";
        category.is_leaf = 1;
        category.name = "标签分类";
        category.save();
    }
}