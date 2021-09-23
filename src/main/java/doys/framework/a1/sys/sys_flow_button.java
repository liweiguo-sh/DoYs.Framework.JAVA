package doys.framework.a1.sys;
import doys.framework.a2.base.BASE_ENTITY;
import doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import doys.framework.upgrade.db.enumeration.EntityTableMatch;

@EntityTableAnnotation(databasePk = "sys", match = EntityTableMatch.appand)
@EntityIndexAnnotation(pk = "flow_pk,button_pk", ix = { "flow_pk" })
public class sys_flow_button extends BASE_ENTITY {
    @EntityFieldAnnotation(length = "30")
    public String icon;
}
