/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-06-19
 * 菜单视图类
 *****************************************************************************/
package com.doys.framework.aid;
import com.doys.framework.core.view.BaseViewController;
import com.doys.framework.util.UtilString;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/framework/aid/menu_view")
public class MenuView extends BaseViewController {
    @Override
    protected boolean BeforeSave(boolean addnew, long id) throws Exception {
        int len;

        String sql;
        String pkParent = in("pkParent");
        String pkMax, pkNew;
        // ------------------------------------------------
        if (addnew) {
            if (pkParent.equalsIgnoreCase("")) {
                err("缺少父级节点参数，请检查。");
                return false;
            }
            len = pkParent.length();
            sql = "SELECT MAX(pk) FROM sys_menu WHERE LEFT(pk, ?) = ? AND LENGTH(pk) = ?";
            pkMax = dbSys.getValue(sql, "", len, pkParent, len + 3);
            if (pkMax.equals("")) {
                pkNew = pkParent + "001";
                // -- 修改上级菜单is_leaf属性 --
                sql = "UPDATE sys_menu SET is_leaf = 0 WHERE pk = ?";
                dbSys.exec(sql, pkParent);
            }
            else {
                pkNew = UtilString.getNewNodeKey(pkMax, 3);
            }
            this.setFormValue("pk", pkNew);
            this.setFormValue("is_leaf", 1);
        }
        return true;
    }

    @Override
    protected boolean BeforeDelete(long id) throws Exception {
        int result, len;

        String sql;
        String pk = in("pk");
        // ------------------------------------------------
        len = pk.length();
        sql = "SELECT COUNT(1) FROM sys_menu WHERE LEFT(pk, ?) = ? AND LENGTH(pk) = ?";
        result = dbSys.getInt(sql, 0, len, pk, len + 3);
        if (result > 0) {
            err("当前菜单存在下级子菜单，不能删除。");
            return false;
        }
        // ------------------------------------------------
        return true;
    }
    @Override
    protected boolean AfterDelete(long id) throws Exception {
        int result, len;

        String sql;
        String pkParent, pk = in("pk");
        // ------------------------------------------------
        if (pk.length() > 6) {
            pkParent = pk.substring(0, pk.length() - 3);
            len = pkParent.length();
            sql = "SELECT COUNT(1) FROM sys_menu WHERE LEFT(pk, ?) = ? AND LENGTH(pk) = ?";
            result = dbSys.getInt(sql, 0, len, pkParent, len + 3);

            if (result == 0) {
                sql = "UPDATE sys_menu SET is_leaf = 1 WHERE pk = ?";
                dbSys.exec(sql, pkParent);
            }
        }
        // ------------------------------------------------
        return true;
    }
}