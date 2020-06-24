package com.doys.aprint.task;
import com.doys.framework.database.DBFactory;
import com.doys.framework.dts.parent.EntityTable;
import com.doys.framework.util.UtilDate;
public class TaskService {
    public static int createQuickPrintTask(DBFactory dbBus, int labelId, String userkey) throws Exception {
        String taskPk;

        EntityTable et;

        // ------------------------------------------------
        taskPk = "" + UtilDate.getDateTimeStr();

        et = new EntityTable(dbBus, "core_task");
        et.setValue("pk", taskPk)
            .setValue("label_id", labelId)
            .setValue("bus_date", UtilDate.getDateStr())
            .setValue("creator", userkey);
        et.Save();

        return (int) et.getId();
    }
}