package doys.framework.a1.prefix;
import doys.framework.a2.base.BASE_ENTITY_CRUD;
import doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import doys.framework.upgrade.db.enum1.EntityFieldType;
import doys.framework.upgrade.db.enum1.EntityTableMatch;

@EntityTableAnnotation(databasePk = "prefix", match = EntityTableMatch.strict)
@EntityIndexAnnotation(pk = "id", ux = { "pk" })
public class sys_user extends BASE_ENTITY_CRUD {
    @EntityFieldAnnotation(length = "20")
    public String pk;

    @EntityFieldAnnotation(length = "20")
    public String name;
    @EntityFieldAnnotation(length = "32")
    public String password;

    @EntityFieldAnnotation(type = EntityFieldType.TINYINT, text = "禁用")
    public int flag_disabled = 0;

    @EntityFieldAnnotation(type = EntityFieldType.TINYINT, text = "菜单过期标志", comment = "0：正常；1：需要重新计算用户的菜单访问权限")
    public int flag_menu_overdue = 0;

    @EntityFieldAnnotation(type = EntityFieldType.TINYINT, text = "内置用户", comment = "内置用户不允许删除")
    public int flag_built_in = 0;

    @EntityFieldAnnotation(length = "100")
    public String remark;
}
