package com.doys.framework.dts.sys;
import com.doys.framework.dts.base.BASE_ENTITY_AUTO;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import com.doys.framework.upgrade.db.enum1.EntityFieldType;
import com.doys.framework.upgrade.db.enum1.EntityTableMatch;

@EntityTableAnnotation(databasePk = "sys", match = EntityTableMatch.appand)
@EntityIndexAnnotation(pk = "id", ux = { "view_pk,name" }, ix = { "view_pk" })
public class sys_view_field extends BASE_ENTITY_AUTO {
    @EntityFieldAnnotation(type = EntityFieldType.LONG, auto = true, not_null = true, comment = "主键")
    public long id;

    @EntityFieldAnnotation(type = EntityFieldType.TINYINT)
    public int flag_pkey;
    @EntityFieldAnnotation(type = EntityFieldType.TINYINT)
    public int flag_identity;
    @EntityFieldAnnotation(type = EntityFieldType.TINYINT)
    public int flag_nullable;
}
