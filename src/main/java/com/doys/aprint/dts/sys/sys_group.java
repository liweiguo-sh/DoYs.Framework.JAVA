package com.doys.aprint.dts.sys;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;

@EntityTableAnnotation(databasePk = "prefix")
public class sys_group {
    @EntityFieldAnnotation(length = "20")
    public String name;
}
