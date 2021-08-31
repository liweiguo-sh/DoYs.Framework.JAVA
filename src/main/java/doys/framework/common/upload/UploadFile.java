/******************************************************************************
 * Copyright (C), 2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2021-08-28
 * @modify_date 2021-08-31
 * 通用文件上传controller
 *****************************************************************************/
package doys.framework.common.upload;
import doys.framework.core.base.BaseControllerTenant;
import doys.framework.core.entity.RestResult;
import doys.framework.util.UtilFile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/framework/common/upload/")
public class UploadFile extends BaseControllerTenant {
    private static String relativePathBase = "/framework/upload";
    // ------------------------------------------------------------------------
    @RequestMapping(value = "/upload_temp_file")
    private RestResult uploadDraftFile(@RequestParam("file") MultipartFile multipartFile) {
        long size = multipartFile.getSize();

        String suffix, name, originalName, extName, pathname;
        String sizeText = UtilFile.getSizeText(size);
        String relativePath = relativePathBase + "/temp";
        // ------------------------------------------------
        try {
            originalName = multipartFile.getOriginalFilename();
            extName = UtilFile.getExtName(originalName);
            suffix = UtilFile.getFileSN();
            name = UtilFile.getNewName(originalName, suffix);
            pathname = UtilFile.saveUploadFileTemp(multipartFile, relativePath, name);

            // -- 返回结果 --
            ok("name", name);
            ok("originalName", originalName);
            ok("extName", extName);
            ok("pathname", pathname);
            ok("sizeText", sizeText);
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}