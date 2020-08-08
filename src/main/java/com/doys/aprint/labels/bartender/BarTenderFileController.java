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
@RequestMapping("/aprint/labels/bartender")
public class BarTenderFileController extends BaseController {
    @RequestMapping(value = "/upload")
    private RestResult uploadFile(@RequestParam("file") MultipartFile multipartFile, @RequestParam("labelId") int labelId,
                                  @RequestParam("labelType") String labelType, @RequestParam("fileType") String fileType) {
        String filename, pathname;
        String[] ret;
        // ------------------------------------------------
        try {
            // -- 1. 保存标签模板文件或标签数据源模板文件 --
            ret = LabelFileService.SaveLabelFile(dbBus, multipartFile, labelId, labelType, fileType);
            filename = ret[0];
            pathname = ret[1];
            ok("filename", filename);

            // -- 2. 保存标签变量 --
            if (labelType.equalsIgnoreCase("BarTender")) {
                if (fileType.equalsIgnoreCase("data")) {
                    BarTenderFileService.dataFileToLabelVariable(dbBus, labelId, pathname);
                }
            }

            // -- 3. 更新标签数据表结构 --
            if (labelType.equalsIgnoreCase("BarTender")) {
                if (fileType.equalsIgnoreCase("data")) {
                    LabelTableService.labelVariableToLabelColumn(dbBus, labelId);
                }
            }
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}