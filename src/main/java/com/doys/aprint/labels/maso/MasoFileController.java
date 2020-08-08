package com.doys.aprint.labels.maso;
import com.doys.aprint.labels.LabelTableService;
import com.doys.framework.config.Const;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/aprint/labels/maso/MasoFile")
public class MasoFileController extends BaseController {
    @RequestMapping("/saveLabelContent")
    private RestResult saveLabelContent() {
        int labelId = inInt("id");

        String sql;
        String content = in("content");
        String vars = in("vars");

        ArrayList<HashMap<String, Object>> listVars = new ArrayList<>();
        // ------------------------------------------------
        try {
            sql = "UPDATE base_label SET content = ?, vars = ? WHERE id = ?";
            dbBus.exec(sql, content, vars, labelId);

            // -- 1. 解析标签变量 -------------------------------
            String[] arrVars = vars.split(Const.CHAR3);
            for (int i = 0; i < arrVars.length; i++) {
                String[] arrVar = arrVars[i].split(Const.CHAR4);
                String name = arrVar[0];
                String value = (arrVar.length == 2 ? arrVar[1] : "");
                if (name.contains("标签元素") || name.contains("共享变量")) {
                    continue;
                }

                HashMap<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("value", value);
                listVars.add(map);
            }

            // -- 2. 保存标签变量 --
            MasoFileService.masoVariableToLabelVariable(dbBus, labelId, listVars);

            // -- 3. 更新标签数据表结构 --
            LabelTableService.labelVariableToLabelColumn(dbBus, labelId);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}
