package com.doys.framework.dts.sys;
import com.doys.framework.dts.parent.TOP_ENTITY;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
@EntityTableAnnotation
public class sys_tree extends TOP_ENTITY {
    @EntityFieldAnnotation(length = "20")
    String databasePk;
}
