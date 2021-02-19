/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-06-19
 * 菜单视图类
 *****************************************************************************/
package doys.framework.aid;
import doys.framework.core.ex.UnImplementException;
import doys.framework.core.view.BaseViewControllerTenant;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/framework/aid/database_view")
public class DatabaseViewTenant extends BaseViewControllerTenant {
    @Override
    protected boolean ButtonClick(long id, SqlRowSet rsViewButton, String buttonName) throws Exception {
        String databaseType = in("type");
        // ------------------------------------------------
        if (buttonName.equalsIgnoreCase("refresh")) {
            DBSchema schema = new DBSchema(dbSys);
            schema.refreshDBStruct(databaseType, "");
        }
        else {
            throw new UnImplementException();
        }
        return true;
    }
}