package com.doys.aprint.dts;
import com.doys.framework.dts.parent.BASE_CRUD;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;

@EntityTableAnnotation(databasePk = "prefix")
public class sys_group extends BASE_CRUD {
    @EntityFieldAnnotation(length = "20")
    public String name;
}
