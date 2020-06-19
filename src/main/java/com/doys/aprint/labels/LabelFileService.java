package com.doys.aprint.labels;
import com.doys.framework.core.base.BaseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
@Component
public class LabelFileService extends BaseService {
    @Value("${global.resRunPath}")
    private String _mResRunPath;
    @PostConstruct
    public void getResRunPath() {
        resRunPath = _mResRunPath;
    }

    private static String resRunPath;
    // ------------------------------------------------------------------------
    public static void SaveLabelFiel(int labelId, String type, MultipartFile multipartFile) throws Exception {
        String path, extname, filename;

        File filePath, fileLabel;
        // -- extname -------------------------------------
        if (type.equalsIgnoreCase("BarTender")) {
            extname = "btw";
        }
        else {
            throw new Exception("unsupport label type: " + type);
        }

        // -- path ----------------------------------------
        path = resRunPath + "/aprint/label_file/";
        filePath = new File(path);
        if (!filePath.exists()) {
            if (!filePath.mkdirs()) {
                throw new Exception("创建标签文件目录失败，请检查。");
            }
        }

        // -- save label file -----------------------------
        filename = path + labelId + "." + extname;
        fileLabel = new File(filename);
        multipartFile.transferTo(fileLabel);
    }
}