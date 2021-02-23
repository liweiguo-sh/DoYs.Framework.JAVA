/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-06-19
 * 菜单视图类
 *****************************************************************************/
package doys.framework.aid;
import doys.framework.a2.ViewControllerSys;
import doys.framework.core.ex.UnImplementException;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/framework/aid/database_view")
public class DatabaseView extends ViewControllerSys {
    @Override
    protected boolean ButtonClick(long id, SqlRowSet rsViewButton, String buttonName) throws Exception {
        String pk = in("pk");
        // ------------------------------------------------
        if (buttonName.equalsIgnoreCase("refresh")) {
            DBSchema schema = new DBSchema(dbSys);
            schema.refreshDBStruct(pk, "");
        }
        else {
            throw new UnImplementException();
        }
        return true;
    }
}