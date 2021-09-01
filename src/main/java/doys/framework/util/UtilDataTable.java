package doys.framework.util;
import doys.framework.database.DBFactory;
import doys.framework.database.dtb.DataTable;

public class UtilDataTable {
    public static DataTable getDataTableByExcelArray(DBFactory dbBus, String[][] data) throws Exception {
        int rowCount, columnCount;
        String sql;

        DataTable dtb;
        DataTable.DataRow dataRow;
        // --------------------------------------------------------------------
        rowCount = data.length - 1;
        columnCount = data[0].length;
        sql = "SELECT '" + UtilString.arrayJoin(data[0], "', '") + "'";
        dtb = dbBus.getDataTable(sql);

        for (int iRow = 1; iRow < rowCount; iRow++) {
            dataRow = dtb.NewRow();
            for (int iCol = 0; iCol < columnCount; iCol++) {
                dataRow.setDataCell(iCol + 1, data[iRow][iCol]);
            }
            dtb.AddRow(dataRow);
        }
        return dtb;
    }
}
