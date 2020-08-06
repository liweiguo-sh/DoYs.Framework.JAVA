package com.doys.framework.dts.base;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.enum1.EntityFieldType;

import java.util.Date;

public class BASE_ENTITY_CRUD extends BASE_ENTITY_AUTO {
    @EntityFieldAnnotation(length = "20", not_null = true)
    public String creator;
    @EntityFieldAnnotation(type = EntityFieldType.DATETIME, not_null = true, default_value = "CURRENT_TIMESTAMP")
    public Date cdate;

    @EntityFieldAnnotation(length = "20")
    public String modifier;
    @EntityFieldAnnotation(type = EntityFieldType.DATETIME, default_value = "CURRENT_TIMESTAMP")
    public Date mdate;
}