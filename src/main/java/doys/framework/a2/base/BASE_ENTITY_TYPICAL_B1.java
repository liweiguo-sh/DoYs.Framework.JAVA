/******************************************************************************
 * Copyright (C), 2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2021-03-30
 * 典型实体类-业务单据表-B1
 *****************************************************************************/
package doys.framework.a2.base;
import doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import doys.framework.upgrade.db.enum1.EntityFieldType;

import java.util.Date;

public class BASE_ENTITY_TYPICAL_B1 extends BASE_ENTITY_AUTO {
    @EntityFieldAnnotation(text = "单号", length = "20", not_null = true)
    public String pk;
    @EntityFieldAnnotation(text = "业务日期", type = EntityFieldType.DATE, not_null = true)
    public java.sql.Date bus_date;

    @EntityFieldAnnotation(text = "备注", length = "100")
    public String remark;

    // ----------------------------------------------------
    @EntityFieldAnnotation(length = "20", not_null = true, default_value = "system")
    public String creator;
    @EntityFieldAnnotation(type = EntityFieldType.DATETIME, not_null = true, default_value = "CURRENT_TIMESTAMP")
    public Date cdate;

    @EntityFieldAnnotation(length = "20")
    public String modifier;
    @EntityFieldAnnotation(type = EntityFieldType.DATETIME)
    public Date mdate;

    @EntityFieldAnnotation(default_value = "0")
    public int astatus;
    @EntityFieldAnnotation(length = "20")
    public String auditor;
    @EntityFieldAnnotation(type = EntityFieldType.DATETIME)
    public Date adate;
}