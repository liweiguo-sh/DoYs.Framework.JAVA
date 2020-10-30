package com.doys.aprint.dts.rpt;
import com.doys.framework.dts.base.BASE_ENTITY;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import com.doys.framework.upgrade.db.enum1.EntityTableMatch;

@EntityTableAnnotation(match = EntityTableMatch.strict)
@EntityIndexAnnotation(pk = "task_date")
public class rpt_task_day extends BASE_ENTITY {
    @EntityFieldAnnotation(text = "日期", not_null = true)
    public java.sql.Date task_date;

    @EntityFieldAnnotation(text = "任务数量")
    public int qty_task;
    @EntityFieldAnnotation(text = "打印数量")
    public int qty_print;
}