package doys.framework.a1.prefix;
import doys.framework.a2.base.BASE_ENTITY_CRUD;
import doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import doys.framework.upgrade.db.enumeration.EntityFieldType;
import doys.framework.upgrade.db.enumeration.EntityTableMatch;

@EntityTableAnnotation(databasePk = "prefix", match = EntityTableMatch.strict)
@EntityIndexAnnotation(pk = "id", ux = { "pk" })
public class sys_group extends BASE_ENTITY_CRUD {
    @EntityFieldAnnotation(length = "20", not_null = true)
    public String pk;

    @EntityFieldAnnotation(length = "20")
    public String name;

    @EntityFieldAnnotation(type = EntityFieldType.TINYINT, text = "内置用户组", comment = "内置用户组不允许删除")
    public int flag_built_in = 0;

    @EntityFieldAnnotation(length = "100")
    public String remark;
}
