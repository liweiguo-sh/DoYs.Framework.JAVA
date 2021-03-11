/******************************************************************************
 * Copyright (C), 2020, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-05-06
 * 通用视图controller模板类
 *****************************************************************************/
package doys.framework.system.mock;
import doys.framework.core.base.BaseControllerStd;
import doys.framework.core.entity.RestResult;
import doys.framework.core.ex.CommonException;
import doys.framework.util.UtilDate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

@RestController
@RequestMapping("/system/mock")
public class MockController extends BaseControllerStd {
    @PostMapping("/generate_data")
    public RestResult generateMockData() {
        int rowCount = inInt("rowCount");
        int nIdx = 0, colCount = 0, length = 0;

        String sql, sqlInsert = "", sqlFields = null, sqlValues = null;
        String type = "", text = "", value = "";
        String tableName = in("tableName");

        Object[] parameters;
        ArrayList<Object[]> listInsert = new ArrayList<>();
        Random random = new Random();
        SqlRowSet rs, rsField;
        // ------------------------------------------------
        try {
            sql = "TRUNCATE TABLE " + tableName;
            dbTenant.exec(sql);

            sql = "SELECT * FROM " + tableName;
            rs = dbTenant.getRowSet(sql);
            if (rs.next()) {
                throw new CommonException("表中已存在数据，不允许生成模拟数据。");
            }

            sql = "SELECT name, text, type, length FROM sys_field WHERE table_pk = ? AND flag_identity = 0";
            rsField = dbSys.getRowSet(sql, "prefix." + tableName);
            while (rsField.next()) {
                if (sqlFields == null) {
                    sqlFields = "";
                    sqlValues = "?";
                }
                else {
                    sqlFields += ", ";
                    sqlValues += ", ?";
                }
                sqlFields += rsField.getString("name");
                colCount++;
            }

            sqlInsert = "INSERT INTO " + tableName + " (" + sqlFields + ") VALUES (" + sqlValues + ")";
            for (int i = 0; i < rowCount; i++) {
                nIdx = 0;
                parameters = new Object[colCount];
                rsField.beforeFirst();
                while (rsField.next()) {
                    type = rsField.getString("type");
                    text = rsField.getString("text");
                    length = rsField.getInt("length");

                    if (type.equals("string")) {
                        value = text + "_" + (i + 1);
                        if (value.length() > length) {
                            value = value.substring(0, length);
                        }
                    }
                    else if (type.equals("datetime")) {
                        value = UtilDate.getDateTimeString();
                    }
                    else {
                        value = String.valueOf(random.nextInt(100) + 1);
                    }
                    parameters[nIdx++] = value;
                }
                listInsert.add(parameters);
            }

            LocalDateTime dtStart = LocalDateTime.now();
            int[] arrResult = dbTenant.batchUpdate(sqlInsert, listInsert);
            logger.info("生成记录条数：" + arrResult.length + "。 耗时：" + UtilDate.getDateTimeDiff(dtStart) + "ms.");
        } catch (Exception e) {
            return ResultErr(e);
        }
        return ResultOk();
    }
}