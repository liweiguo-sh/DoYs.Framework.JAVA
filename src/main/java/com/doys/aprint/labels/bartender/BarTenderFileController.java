package com.doys.aprint.labels.bartender;
import com.doys.aprint.labels.LabelFileService;
import com.doys.framework.core.base.BaseController;
import com.doys.framework.core.db.DBFactory;
import com.doys.framework.core.entity.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/aprint/labels/bartender")
public class BarTenderFileController extends BaseController {
    @Autowired
    DBFactory dbSys;

    @RequestMapping(value = "/upload")
    private RestResult uploadFile(@RequestParam("file") MultipartFile multipartFile, @RequestParam("labelId") int labelId) {
        // ------------------------------------------------
        try {
            LabelFileService.SaveLabelFiel(labelId, "BarTender", multipartFile);
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
        }
        return ResultOk();
    }
}