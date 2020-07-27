package com.doys.aprint.base;
import com.doys.framework.core.view.BaseViewController;
import com.doys.framework.upgrade.db.util.MySqlHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;

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
        int valueLen = inInt("value_len");

        String type = in("type");
        String name = in("name");
        String value = in("value");
        String tableName;
        // ------------------------------------------------
        tableName = LabelTableService.getLabelXTableName(labelId);
        if (MySqlHelper.hasColumn(dbBus, tableName, name)) {
            MySqlHelper.dropColumn(dbBus, tableName, name);
        }

        if (!type.equalsIgnoreCase("fixed")) {
            HashMap<String, Object> mapVar = new HashMap<>();
            mapVar.put("name", name);
            mapVar.put("value", value);
            mapVar.put("value_len", valueLen);
            mapVar.put("type", type);

            ArrayList<HashMap<String, Object>> listVar = new ArrayList<>();
            listVar.add(mapVar);
            LabelTableService.dynamicAddLabelTableColumn(dbBus, labelId, listVar);
        }
        return true;
    }
}