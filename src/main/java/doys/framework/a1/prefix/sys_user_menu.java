package doys.framework.a1.prefix;
import doys.framework.a2.base.BASE_ENTITY;
import doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import doys.framework.upgrade.db.enum1.EntityTableMatch;

@EntityTableAnnotation(databasePk = "prefix", match = EntityTableMatch.strict)
@EntityIndexAnnotation(pk = "user_pk, menu_pk", ix = { "user_pk", "menu_pk" })
public class sys_user_menu extends BASE_ENTITY {
    @EntityFieldAnnotation(length = "20", not_null = true)
    public String user_pk;

    @EntityFieldAnnotation(length = "15", not_null = true)
    public String menu_pk;
}