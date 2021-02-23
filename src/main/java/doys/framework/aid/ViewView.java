/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-06-19
 * 菜单视图类
 *****************************************************************************/
package doys.framework.aid;
import doys.framework.a2.ViewControllerSys;
import doys.framework.system.service.ViewService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/framework/aid/view_view")
public class ViewView extends ViewControllerSys {
    @Override
    protected boolean BeforeSave(boolean addnew, long id) throws Exception {
        String tablePk = in("table_pk");
        String tableName;
        String[] arr;
        // ------------------------------------------------
        arr = tablePk.split("\\.");
        if (arr.length == 2) {
            tableName = arr[1];
            this.setFormValue("table_name", tableName);
        }
        // ------------------------------------------------
        return true;
    }
    @Override
    protected boolean AfterSave(boolean addnew, long id) throws Exception {
        String viewPk = in("pk");
        // ------------------------------------------------
        ViewService.refreshViewField(dbSys, dbSys, viewPk);
        // ------------------------------------------------
        return true;
    }

    @Override
    protected boolean AfterDelete(long id) throws Exception {
        String sql;
        String pk = in("pk");

        // ------------------------------------------------
        sql = "DELETE FROM sys_view_field WHERE view_pk = ?";
        dbSys.exec(sql, pk);

        sql = "DELETE FROM sys_view_button WHERE view_pk = ?";
        dbSys.exec(sql, pk);
        // ------------------------------------------------
        return true;
    }
}