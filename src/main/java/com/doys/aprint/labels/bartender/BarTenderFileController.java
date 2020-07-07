package com.doys.aprint.labels.bartender;
import com.doys.aprint.labels.LabelFileService;
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
        // ------------------------------------------------
        try {
            String fileName = LabelFileService.SaveLabelFile(dbBus, multipartFile, labelId, labelType, fileType);

            ok("filename", fileName);
        } catch (Exception e) {
            return ResultErr(e);
        } finally {
        }
        return ResultOk();
    }
}