package com.doys.aprint.label;
import com.doys.aprint.labels.LabelFileService;
import com.doys.aprint.labels.LabelTableService;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/aprint/dlabel")
public class DLabelController extends BaseController {
    @RequestMapping("/getLabelForDesign")
    private RestResult getLabelContentById() {
        int labelId = inInt("labelId");

        String sql;
        SqlRowSet rsLabel, rsLabelVariable;
        // ------------------------------------------------
        try {
            sql = "SELECT content FROM base_label WHERE id = ?";
            rsLabel = dbBus.getRowSet(sql, labelId);
            ok("dtbLabel", rsLabel);

            sql = "SELECT type, name, value FROM base_label_variable WHERE label_id = ?";
            rsLabelVariable = dbBus.getRowSet(sql, labelId);
            ok("dtbLabelVariable", rsLabelVariable);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
    @RequestMapping("/saveLabelForDesign")
    private RestResult saveLabelForDesign() {
        int labelId = inInt("id");

        String sql;
        String content = in("content");
        // ------------------------------------------------
        try {
            // -- 1. 保存标签内容 --
            sql = "UPDATE base_label SET content = ?, mdate = now() WHERE id = ?";
            dbBus.exec(sql, content, labelId);

            // -- 2. 更新标签数据表结构 --
            LabelTableService.labelVariableToLabelColumn(dbBus, labelId);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }

    @RequestMapping(value = "/uploadLabelVariableImage")
    private RestResult uploadLabelFile(@RequestParam("file") MultipartFile multipartFile, @RequestParam("labelVariableId") int labelVariableId) {
        String filename;
        String[] ret;
        // ------------------------------------------------
        try {
            ret = LabelFileService.saveLabelVariableImage(dbBus, multipartFile, labelVariableId);
            filename = ret[0];

            ok("filename", filename);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}