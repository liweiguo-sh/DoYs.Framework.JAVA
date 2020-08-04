package com.doys.aprint.task;
import com.doys.framework.database.DBFactory;
import com.doys.framework.dts.base.ENTITY_RECORD;
import com.doys.framework.util.UtilDate;
import com.doys.framework.util.UtilString;
public class TaskService {
    public static int createQuickPrintTask(DBFactory dbBus, int labelId, String userPk) throws Exception {
        String taskPk;

        ENTITY_RECORD et;

        // ------------------------------------------------
        taskPk = UtilString.getSN(dbBus, "task_pk", "", "T-{yy}{mm}{dd}-{5}");

        et = new ENTITY_RECORD(dbBus, "core_task");
        et.setValue("pk", taskPk)
            .setValue("label_id", labelId)
            .setValue("bus_date", UtilDate.getDateStr())
            .setValue("creator", userPk);
        et.Save();

        return (int) et.getId();
    }
}