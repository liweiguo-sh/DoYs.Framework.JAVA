/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-08-18
 * 临时文件上传类
 *****************************************************************************/
package doys.framework.util;
import doys.framework.a2.structure.EntityUploadFile;
import doys.framework.core.ex.CommonException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class UtilUpload {
    /**
     * 保存上传文件(临时文件)
     *
     * @param multipartFile
     * @return 返回文件实体
     */
    public static EntityUploadFile saveAsTempFile(MultipartFile multipartFile) throws Exception {
        EntityUploadFile entity = new EntityUploadFile();
        File filePath, fileSave;

        // -- path、name and pathname ----------------------
        entity.setPath(UtilYml.getTempPath("framework"));
        entity.setName(UtilFile.getNewName(multipartFile.getOriginalFilename(), UtilFile.getFileSN()));

        filePath = new File(entity.getPath());
        if (!filePath.exists()) {
            if (!filePath.mkdirs()) {
                throw new CommonException("创建文件目录失败，请检查。");
            }
        }

        // -- save upload file ----------------------------
        fileSave = new File(entity.getPathname());
        multipartFile.transferTo(fileSave);

        // ------------------------------------------------
        return entity;
    }

    public static EntityUploadFile saveUpload(MultipartFile multipartFile, String relativePath, String prefix) throws Exception {
        EntityUploadFile entity = new EntityUploadFile();
        File filePath, fileSave;

        // -- path、name and pathname ----------------------
        entity.setPath(UtilYml.getRunPath(relativePath));
        entity.setOriginalName(multipartFile.getOriginalFilename());
        entity.setName(UtilFile.getNewName(entity.getOriginalName(), prefix));

        filePath = new File(entity.getPath());
        if (!filePath.exists()) {
            if (!filePath.mkdirs()) {
                throw new CommonException("创建文件目录失败，请检查。");
            }
        }

        // -- save upload file ----------------------------
        fileSave = new File(entity.getPathname());
        multipartFile.transferTo(fileSave);

        // ------------------------------------------------
        return entity;
    }
}