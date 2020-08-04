package com.doys.framework.dts.sys;
import com.doys.framework.dts.base.BASE_ENTITY;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;

@EntityTableAnnotation(databasePk = "sys")
@EntityIndexAnnotation(pk = "pk", ix = { "database_pk" })
public class sys_tree extends BASE_ENTITY {
    @EntityFieldAnnotation(length = "20")
    String database_pk;

    @EntityFieldAnnotation(length = "30", not_null = true)
    String pk;
}