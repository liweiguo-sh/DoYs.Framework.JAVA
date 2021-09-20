/******************************************************************************
 * Copyright (C), 2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2021-03-30
 * 基础实体类(CRUD)
 *****************************************************************************/
package doys.framework.a2.base;
import doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import doys.framework.upgrade.db.enum1.EntityFieldType;

import java.util.Date;

public class BASE_ENTITY_CRUD extends BASE_ENTITY_AUTO {
    @EntityFieldAnnotation(length = "20", not_null = true, default_value = "system")
    public String creator;
    @EntityFieldAnnotation(type = EntityFieldType.DATETIME, not_null = true, default_value = EntityFieldAnnotation.now)
    public Date cdate;

    @EntityFieldAnnotation(length = "20")
    public String modifier;
    @EntityFieldAnnotation(type = EntityFieldType.DATETIME)
    public Date mdate;
}
