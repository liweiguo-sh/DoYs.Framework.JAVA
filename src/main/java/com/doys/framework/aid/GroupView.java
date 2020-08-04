package com.doys.framework.aid;
import com.doys.framework.core.view.BaseViewController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/framework/aid/grou_view")
public class GroupView extends BaseViewController {
    private static String inactiveKeys = "developer,everyone,platform,root,always";
    @Override
    protected boolean BeforeSave(boolean addnew, long id) throws Exception {
        String sql;
        String pk = in("pk");
        String name = in("name");

        String[] arrKey = inactiveKeys.split(",");
        // ------------------------------------------------
        if (pk.length() < 3) {
            err("组标识长度不能小于3，请检查。");
            return false;
        }

        for (int i = 0; i < arrKey.length; i++) {
            if (pk.equalsIgnoreCase(arrKey[i]) || pk.equalsIgnoreCase(arrKey[i] + "s")) {
                err(pk + " 是内置保留用户(组)，不允许使用。");
                return false;
            }
        }

        if (name.equals("")) {
            this.setFormValue("name", pk);
        }
        // ------------------------------------------------
        sql = "SELECT COUNT(1) FROM sys_user WHERE pk = ?";
        if (dbBus.getInt(sql, 0, pk) > 0) {
            err("已存在标识为 " + pk + " 的用户，请检查。");
            return false;
        }
        // ------------------------------------------------
        return true;
    }
}