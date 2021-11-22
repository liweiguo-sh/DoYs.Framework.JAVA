package doys.framework.a1.sys;
import doys.framework.a2.base.BASE_ENTITY;
import doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import doys.framework.upgrade.db.enumeration.EntityTableMatch;

@EntityTableAnnotation(databasePk = "sys", match = EntityTableMatch.strict, text = "session值表")
@EntityIndexAnnotation(pk = "token_id,token_value")
public class sys_token_value extends BASE_ENTITY {
    @EntityFieldAnnotation(length = "32", not_null = true)
    public String token_id;

    @EntityFieldAnnotation(length = "250")
    public String token_value;
}