package doys.framework.a1.sys;
import doys.framework.a2.base.BASE_ENTITY;
import doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import doys.framework.upgrade.db.annotation.EntityTableAnnotation;

@EntityTableAnnotation(databasePk = "sys")
@EntityIndexAnnotation(pk = "pk", ix = { "database_pk" })
public class sys_tree extends BASE_ENTITY {
    @EntityFieldAnnotation(length = "20")
    String database_pk;

    @EntityFieldAnnotation(length = "30", not_null = true)
    String pk;
}