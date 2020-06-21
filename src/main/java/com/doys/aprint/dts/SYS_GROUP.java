package com.doys.aprint.dts;
import com.doys.framework.dts.BASE_CRUD;
import com.doys.framework.upgrade.db.annotation.EntityClassAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;

@EntityClassAnnotation(databasePk = "prefix")
public class SYS_GROUP extends BASE_CRUD {
    @EntityFieldAnnotation(length = "20")
    public String name;
}
