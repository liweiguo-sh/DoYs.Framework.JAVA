package com.doys.aprint.labels;
import com.doys.framework.core.base.BaseService;
import com.doys.framework.database.DBFactory;
import com.doys.framework.util.UtilFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
@Component
public class LabelFileService extends BaseService {
    @Value("${global.resRunPath}")
    private String _mResRunPath;

    private static String resRunPath;
    @PostConstruct
    public void getResRunPath() {
        resRunPath = _mResRunPath;
    }
    // ------------------------------------------------------------------------
    public static String SaveLabelFile(DBFactory dbBus, MultipartFile multipartFile, int labelId, String labelType, String fileType) throws Exception {
        String sql;
        String path, name, pathname;

        File filePath, fileLabel;
        // -- pre-processing ------------------------------
        if (!labelType.equalsIgnoreCase("BarTender")) {
            throw new Exception("unsupport label type: " + labelType);
        }
        // -- name and path -------------------------------
        name = UtilFile.getNewName(multipartFile.getOriginalFilename(), String.valueOf(labelId));
        path = resRunPath + "/aprint/label_file/";
        filePath = new File(path);
        if (!filePath.exists()) {
            if (!filePath.mkdirs()) {
                throw new Exception("创建标签文件目录失败，请检查。");
            }
        }
        pathname = path + name;

        // -- save label file -----------------------------
        fileLabel = new File(pathname);
        multipartFile.transferTo(fileLabel);

        // -- save filename -------------------------------
        if (fileType.equalsIgnoreCase("label")) {
            sql = "UPDATE base_label SET label_file_name = ? WHERE id = ?";
            dbBus.exec(sql, name, labelId);
        }
        else {
            sql = "UPDATE base_label SET data_file_name = ? WHERE id = ?";
            dbBus.exec(sql, name, labelId);
        }
        return name;
    }
}