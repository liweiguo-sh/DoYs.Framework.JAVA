package com.doys.framework.dts.prefix;
import com.doys.framework.dts.base.BASE_ENTITY;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import com.doys.framework.upgrade.db.enum1.EntityTableMatch;

@EntityTableAnnotation(databasePk = "prefix", match = EntityTableMatch.strict)
@EntityIndexAnnotation(pk = "group_pk, user_pk", ix = { "group_pk", "user_pk" })
public class sys_group_user extends BASE_ENTITY {
    @EntityFieldAnnotation(length = "20", not_null = true)
    public String group_pk;

    @EntityFieldAnnotation(length = "20", not_null = true)
    public String user_pk;
}