package doys.framework.a1.prefix;
import doys.framework.a2.base.BASE_ENTITY;
import doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import doys.framework.upgrade.db.enum1.EntityTableMatch;

@EntityTableAnnotation(databasePk = "prefix", match = EntityTableMatch.strict)
@EntityIndexAnnotation(pk = "menu_pk, user_group_pk", ix = { "menu_pk", "user_group_pk" })
public class sys_menu_acl extends BASE_ENTITY {
    @EntityFieldAnnotation(length = "15", not_null = true)
    public String menu_pk;

    @EntityFieldAnnotation(length = "15", not_null = true)
    public String user_group_pk;
}
