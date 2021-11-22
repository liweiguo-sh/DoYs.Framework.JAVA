package doys.framework.a1.sys;
import doys.framework.a2.base.BASE_ENTITY;
import doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import doys.framework.upgrade.db.enumeration.EntityTableMatch;

import java.time.LocalDateTime;

@EntityTableAnnotation(databasePk = "sys", match = EntityTableMatch.strict, text = "session表")
@EntityIndexAnnotation(pk = "token_id", ix = { "tenant_id", "user_pk" })
public class sys_token extends BASE_ENTITY {
    @EntityFieldAnnotation(length = "32", not_null = true)
    public String token_id;

    @EntityFieldAnnotation(not_null = true)
    public int tenant_id;
    @EntityFieldAnnotation(length = "20", not_null = true)
    public String user_pk;

    @EntityFieldAnnotation(text = "首次登录时间", not_null = true)
    public LocalDateTime login_time;
    @EntityFieldAnnotation(text = "末次访问时间", not_null = true)
    public LocalDateTime renew_time;
}