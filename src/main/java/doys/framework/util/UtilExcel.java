/******************************************************************************
 * Copyright (C), 2020-2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2020-08-19
 * @modify_date 2021-09-07
 * Excel工具类
 *****************************************************************************/
package doys.framework.util;
import doys.framework.core.ex.CommonException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.text.DecimalFormat;

public class UtilExcel {
    public static String[][] excelToArray(String fileExcel) throws Exception {
        int rowMax, columnCount;
        int idxRow = 0, iRow = 0, iCol = 0;

        String extName, cellValue;
        String[][] data, dataTemp;

        Workbook workbook;
        Sheet sheet;
        Row row, rowHeader;
        Cell cell;
        CellType cellType;
        DecimalFormat df = new DecimalFormat("0");
        // ------------------------------------------------
        try (FileInputStream fis = new FileInputStream(fileExcel)) {
            // -- 1. open workbook ----------------------------
            extName = UtilFile.getExtName(fileExcel);
            if (extName.equalsIgnoreCase("xls")) {
                workbook = new HSSFWorkbook(fis);
            }
            else if (extName.equalsIgnoreCase("xlsx")) {
                workbook = new XSSFWorkbook(fis);
            }
            else {
                throw new CommonException("非法的Excle文件后缀名 (" + extName + "), 请检查。");
            }

            // -- 2. open sheet -------------------------------
            sheet = workbook.getSheetAt(0);
            rowMax = sheet.getLastRowNum();
            if (rowMax < 0) {
                return new String[0][0];
            }
            rowHeader = sheet.getRow(0);
            columnCount = rowHeader.getLastCellNum();
            data = new String[rowMax + 1][columnCount];

            // -- 3. fill data --------------------------------
            for (iRow = 0; iRow <= rowMax; iRow++) {
                row = sheet.getRow(iRow);
                if (row == null) continue;

                for (iCol = 0; iCol < columnCount; iCol++) {
                    cell = row.getCell(iCol);
                    if (cell == null) {
                        data[idxRow][iCol] = "";
                        continue;
                    }
                    // ----------------------------------------
                    cellType = cell.getCellType();
                    if (cellType == CellType.NUMERIC) {
                        cellValue = df.format(cell.getNumericCellValue());
                        if (cellValue.endsWith(".0")) {
                            cellValue = cellValue.substring(0, cellValue.length() - 2);
                        }
                    }
                    else if (cellType == CellType.STRING) {
                        cellValue = cell.getStringCellValue().trim();
                    }
                    else if (cellType == CellType.BLANK) {
                        cellValue = "";
                    }
                    else {
                        cellValue = "???" + cellType + "???";
                    }
                    data[idxRow][iCol] = cellValue;
                }
                idxRow++;
            }

            // -- 4. 有空行，需要去除 -----------------------------
            if (iRow > idxRow) {
                dataTemp = data;
                data = new String[idxRow][columnCount];
                System.arraycopy(dataTemp, 0, data, 0, idxRow);
            }
        } catch (Exception e) {
            System.err.println("iRow = " + iRow + ", iCol = " + iCol);
            throw e;
        }
        // -- 9. return -----------------------------------
        return data;
    }
}