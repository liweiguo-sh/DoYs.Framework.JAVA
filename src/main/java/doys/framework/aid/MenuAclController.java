package doys.framework.aid;
import doys.framework.core.base.BaseControllerStd;
import doys.framework.core.entity.RestResult;
import doys.framework.database.ds.UtilTDS;
import doys.framework.system.service.UserService;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/framework/aid/menu_acl")
public class MenuAclController extends BaseControllerStd {
    @RequestMapping("/getGroupAndUserAndMenu")
    private RestResult getGroupAndUserAndMenu() {
        boolean isDeveloper = ssBoolean("isDeveloper");
        String sql, sqlSystem, sqlMenu;
        String busDB;
        String userPk = ssValue("userPk");

        SqlRowSet rsGroup, rsUser, rsMenu;
        // ------------------------------------------------
        try {
            sql = "SELECT id, pk, name FROM sys_group WHERE flag_built_in = 0 ORDER BY flag_built_in DESC, pk";
            rsGroup = dbTenant.getRowSet(sql);
            ok("dtbGroup", rsGroup);

            sql = "SELECT id, pk, name FROM sys_user WHERE flag_built_in = 0 AND flag_disabled = 0 AND pk <> ? ORDER BY flag_built_in DESC, pk";
            rsUser = dbTenant.getRowSet(sql, userPk);
            ok("dtbUser", rsUser);

            // --------------------------------------------
            busDB = UtilTDS.getTenantDbName();

            if (isDeveloper) {
                sqlMenu = "SELECT pk, text, LPAD(sequence, 3, 0) sequences FROM sys_system UNION ALL SELECT pk, text, sequences FROM sys_menu";
                sql = "SELECT pk, text FROM (" + sqlMenu + ") t ORDER BY sequences";
            }
            else {
                sqlSystem = "SELECT pk, text, LPAD(sequence, 3, 0) sequences FROM sys_system "
                    + "WHERE pk IN (SELECT menu_pk FROM " + busDB + ".sys_user_menu WHERE user_pk = '" + userPk + "' AND LENGTH(pk) = 3)";
                sqlMenu = "SELECT pk, text, sequences FROM sys_menu " +
                    "WHERE pk IN (SELECT menu_pk FROM " + busDB + ".sys_user_menu WHERE user_pk = '" + userPk + "')";

                sql = "SELECT pk, text FROM (" + sqlSystem + " UNION ALL " + sqlMenu + ") t ORDER BY sequences";
            }
            rsMenu = dbSys.getRowSet(sql);
            ok("dtbMenu", rsMenu);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    @RequestMapping("/saveGroupAcl")
    private RestResult saveGroupAcl() {
        String sql, sysDB;
        String groupPk = in("groupPk");
        String userPks = in("userPks");
        String menuPks = in("menuPks");
        // ------------------------------------------------
        try {
            sysDB = UtilTDS.getSysDbName(dbSys);

            sql = "DELETE FROM sys_menu_acl WHERE user_group_pk = ?";
            dbTenant.exec(sql, groupPk);
            if (menuPks.length() > 0) {
                sql = "INSERT INTO sys_menu_acl (menu_pk, user_group_pk) "
                    + "SELECT pk menu_pk, ? user_group_pk FROM " + sysDB + ".sys_menu WHERE pk IN (" + menuPks + ") AND is_leaf = 1";
                dbTenant.exec(sql, groupPk);
            }

            sql = "DELETE FROM sys_group_user WHERE group_pk = ?";
            dbTenant.exec(sql, groupPk);
            if (userPks.length() > 0) {
                sql = "INSERT INTO sys_group_user (group_pk, user_pk) "
                    + "SELECT ? group_pk, pk user_pk FROM sys_user WHERE pk IN (" + userPks + ")";
                dbTenant.exec(sql, groupPk);
            }

            UserService.setMenuOverdue(dbTenant);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
    @RequestMapping("/saveUserAcl")
    private RestResult saveUserAcl() {
        String sql, sysDB;
        String userPk = in("userPk");
        String groupPks = in("groupPks");
        String menuPks = in("menuPks");
        // ------------------------------------------------
        try {
            sysDB = UtilTDS.getSysDbName(dbSys);

            sql = "DELETE FROM sys_menu_acl WHERE user_group_pk = ?";
            dbTenant.exec(sql, userPk);
            if (menuPks.length() > 0) {
                sql = "INSERT INTO sys_menu_acl (menu_pk, user_group_pk) "
                    + "SELECT pk menu_pk, ? user_group_pk FROM " + sysDB + ".sys_menu WHERE pk IN (" + menuPks + ") AND is_leaf = 1";
                dbTenant.exec(sql, userPk);
            }

            sql = "DELETE FROM sys_group_user WHERE user_pk = ?";
            dbTenant.exec(sql, userPk);
            if (groupPks.length() > 0) {
                sql = "INSERT INTO sys_group_user (group_pk, user_pk) "
                    + "SELECT pk group_pk, ? user_pk FROM sys_group WHERE pk IN (" + groupPks + ")";
                dbTenant.exec(sql, userPk);
            }

            UserService.setMenuOverdue(dbTenant);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    @RequestMapping("/getMenuAndUserByGroup")
    private RestResult getMenuAndUserByGroup() {
        String sql;
        String groupPk = in("groupPk");

        SqlRowSet rsUser, rsMenu;
        // ------------------------------------------------
        try {
            sql = "SELECT menu_pk FROM sys_menu_acl WHERE user_group_pk = ?";
            rsMenu = dbTenant.getRowSet(sql, groupPk);
            ok("dtbMenu", rsMenu);

            sql = "SELECT user_pk FROM sys_group_user WHERE group_pk = ?";
            rsUser = dbTenant.getRowSet(sql, groupPk);
            ok("dtbUser", rsUser);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
    @RequestMapping("/getMenuAndGroupByUser")
    private RestResult getMenuAndGroupByUser() {
        String sql;
        String userPk = in("userPk");

        SqlRowSet rsGroup, rsMenu;
        // ------------------------------------------------
        try {
            sql = "SELECT menu_pk FROM sys_menu_acl WHERE user_group_pk = ?";
            rsMenu = dbTenant.getRowSet(sql, userPk);
            ok("dtbMenu", rsMenu);

            sql = "SELECT group_pk FROM sys_group_user WHERE user_pk = ?";
            rsGroup = dbTenant.getRowSet(sql, userPk);
            ok("dtbGroup", rsGroup);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}