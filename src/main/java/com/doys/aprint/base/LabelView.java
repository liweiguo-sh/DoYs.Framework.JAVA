package com.doys.aprint.base;
import com.doys.aprint.labels.LabelTableService;
import com.doys.framework.core.view.BaseViewController;
import com.doys.framework.upgrade.db.util.MySqlHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aprint/base/label_view")
public class LabelView extends BaseViewController {
    @Override
    protected boolean BeforeSave(boolean addnew, long id) {
        String sql;
        // ------------------------------------------------
        try {
            if (addnew) {
                this.setFormValue("flag_disabled", "0");
            }
        } catch (Exception e) {
            err(e);
            return false;
        }
        return true;
    }
    @Override
    protected boolean AfterSave(boolean addnew, long id) {
        String tableName;
        // ------------------------------------------------
        try {
            tableName = LabelTableService.getLabelXTableName(id);

            if (!MySqlHelper.hasTable(dbBus, tableName)) {
                LabelTableService.createLabelTable(dbBus, tableName);
            }
            LabelTableService.labelVariableToLabelColumn(dbBus, id);
        } catch (Exception e) {
            err(e);
            return false;
        }
        return true;
    }

    @Override
    protected boolean AfterDelete(long id) throws Exception {
        String sql;
        String tableName;
        // ------------------------------------------------
        sql = "DELETE FROM base_label_variable WHERE label_id = ?";
        dbBus.exec(sql, id);

        tableName = LabelTableService.getLabelXTableName(id);
        if (MySqlHelper.hasTable(dbBus, tableName)) {
            sql = "DROP TABLE " + tableName;
            dbBus.exec(sql);
        }
        // ------------------------------------------------
        return true;
    }
}