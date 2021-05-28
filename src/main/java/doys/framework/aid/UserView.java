package doys.framework.aid;
import doys.framework.core.view.BaseViewController;
import doys.framework.util.UtilDigest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/framework/aid/user_view")
public class UserView extends BaseViewController {
    private static String inactiveKeys = "developer,everyone,platform,root,always";
    @Override
    protected boolean BeforeSave(boolean addnew, long id) throws Exception {
        String sql;
        String pk = in("pk");
        String name = in("name");
        String password = in("password");

        String[] arrKey = inactiveKeys.split(",");
        // ------------------------------------------------
        if (pk.length() < 3) {
            err("用户标识长度不能小于3，请检查。");
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
        if (password.length() != 32) {
            password = UtilDigest.passwordMD5(pk.toLowerCase(), password);
            setFormValue("password", password);
        }
        // ------------------------------------------------
        sql = "SELECT COUNT(1) FROM sys_group WHERE pk = ?";
        if (dbBus.getInt(sql, 0, pk) > 0) {
            err("已存在标识为 " + pk + " 的用户组，请检查。");
            return false;
        }
        // ------------------------------------------------
        return true;
    }
}