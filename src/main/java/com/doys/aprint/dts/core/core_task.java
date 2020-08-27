package com.doys.aprint.dts.core;
import com.doys.framework.dts.base.BASE_ENTITY_CRUD;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import com.doys.framework.upgrade.db.enum1.EntityTableMatch;

import java.util.Date;

@EntityTableAnnotation(match = EntityTableMatch.strict)
@EntityIndexAnnotation(pk = "id", ux = { "pk" }, ix = { "label_id" })
public class core_task extends BASE_ENTITY_CRUD {
    public int label_id;

    @EntityFieldAnnotation(text = "任务单号", length = "20")
    public String pk;

    @EntityFieldAnnotation(text = "日期", default_value = "CURRENT_TIMESTAMP")
    public Date bus_date;

    @EntityFieldAnnotation(text = "备注", length = "100")
    public String remark;
}