package doys.framework.a1.sys;
import doys.framework.a2.base.BASE_ENTITY_AUTO;
import doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import doys.framework.upgrade.db.enum1.EntityTableMatch;

@EntityTableAnnotation(databasePk = "sys", match = EntityTableMatch.strict)
@EntityIndexAnnotation(pk = "id", ux = { "pk", "instance_pk,name" })
public class sys_database extends BASE_ENTITY_AUTO {
    @EntityFieldAnnotation(length = "20", comment = "主键")
    public String pk;

    @EntityFieldAnnotation(length = "10", text = "类型", default_value = "", comment = "类型|sys,prefix,common")
    public String type;

    @EntityFieldAnnotation(length = "30", text = "数据库名称", comment = "物理数据库名称。type为prefix时，name必须和pk相同")
    public String name;

    @EntityFieldAnnotation(length = "30", text = "数据库实例", comment = "物理数据库所属实例标识")
    public String instance_pk;

    @EntityFieldAnnotation(length = "200", text = "备注", default_value = "")
    public String remark;
}
