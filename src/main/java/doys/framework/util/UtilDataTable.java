package doys.framework.util;
import doys.framework.database.DBFactory;
import doys.framework.database.dtb.DataTable;
public class UtilDataTable {
    public static DataTable getDataTableByExcelArray(DBFactory dbBus, String[][] data) throws Exception {
        int rowCount, iRow;
        String sql;

        DataTable dtb;

        // --------------------------------------------------------------------
        rowCount = data.length - 1;
        sql = "SELECT " + UtilString.arrayJoin(data[0], ", ");
        dtb = dbBus.getDataTable(sql);

        for (int i = 1; i < data[0].length; i++) {

        }
        return dtb;
    }
}
