package doys.framework.a1.sys;
import doys.framework.a2.base.BASE_ENTITY_AUTO;
import doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import doys.framework.upgrade.db.enum1.EntityTableMatch;

@EntityTableAnnotation(databasePk = "sys", match = EntityTableMatch.strict)
@EntityIndexAnnotation(pk = "id", ux = { "pk" })
public class sys_database extends BASE_ENTITY_AUTO {
    @EntityFieldAnnotation(length = "10", text = "类型", default_value = "", comment = "类型|sys,prefix,common")
    public String type;

    @EntityFieldAnnotation(length = "20", comment = "主键")
    public String pk;
    @EntityFieldAnnotation(length = "20", text = "数据库名称", comment = "物理数据库名称。type为prefix时，name为物理数据库名称前缀")
    public String name;

    @EntityFieldAnnotation(length = "20", text = "数据库类型", default_value = "MySQL", comment = "当前只支持MySQL")
    public String db_type;

    @EntityFieldAnnotation(length = "200", text = "备注", default_value = "")
    public String remark;
}