package com.doys.aprint.labels.bartender;
import com.doys.aprint.labels.LabelFileService;
import com.doys.aprint.labels.LabelTableService;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.entity.RestResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/aprint/labels/BarTenderFile")
public class BarTenderFileController extends BaseController {
    @RequestMapping(value = "/uploadLabelFile")
    private RestResult uploadLabelFile(@RequestParam("file") MultipartFile multipartFile, @RequestParam("labelId") int labelId) {
        String filename;
        String[] ret;
        // ------------------------------------------------
        try {
            ret = LabelFileService.saveLabelFile(dbBus, multipartFile, labelId);
            filename = ret[0];

            ok("filename", filename);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
    @RequestMapping(value = "/uploadDataFile")
    private RestResult uploadDataFile(@RequestParam("file") MultipartFile multipartFile, @RequestParam("labelId") int labelId) {
        String filename, pathname;
        String[] ret;
        // ------------------------------------------------
        try {
            // -- 1. 保存数据模板文件 --
            ret = LabelFileService.saveDataFile(dbBus, multipartFile, labelId);
            filename = ret[0];
            pathname = ret[1];
            ok("filename", filename);

            // -- 2. 保存标签变量 --
            BarTenderFileService.dataFileToLabelVariable(dbBus, labelId, pathname);

            // -- 3. 更新标签数据表结构 --
            LabelTableService.labelVariableToLabelColumn(dbBus, labelId);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}