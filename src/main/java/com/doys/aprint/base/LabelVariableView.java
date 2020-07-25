package com.doys.aprint.base;
import com.doys.framework.core.view.BaseViewController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aprint/base/label_variable_view")
public class LabelVariableView extends BaseViewController {
    @Override
    protected boolean BeforeSave(boolean addnew, long id) throws Exception {
        int result;
        int labelId = inInt("label_id");

        String sql;
        String name = in("name");
        // ------------------------------------------------
        sql = "SELECT COUNT(1) FROM base_label_variable WHERE label_id = ? AND name = ? AND hidden = 1";
        result = dbBus.getInt(sql, 0, labelId, name);
        if (result == 1) {
            sql = "DELETE FROM base_label_variable WHERE label_id = ? AND name = ? AND hidden = 1";
            dbBus.exec(sql, labelId, name);
        }
        // ------------------------------------------------
        return true;
    }
    @Override
    protected boolean AfterSave(boolean addnew, long id) throws Exception {
        int labelId = inInt("label_id");

        LabelTableService.createLabelTable(dbBus, labelId);
        return true;
    }
}