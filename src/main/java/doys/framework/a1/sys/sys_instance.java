package doys.framework.a1.sys;
import doys.framework.a2.base.BASE_ENTITY;
import doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import doys.framework.upgrade.db.enum1.EntityTableMatch;

@EntityTableAnnotation(databasePk = "sys", match = EntityTableMatch.strict)
@EntityIndexAnnotation(pk = "pk")
public class sys_instance extends BASE_ENTITY {
    @EntityFieldAnnotation(length = "30", comment = "主键")
    public String pk;
    @EntityFieldAnnotation(length = "50", text = "数据库实例名称")
    public String name;
    @EntityFieldAnnotation(length = "20", text = "数据库类型", default_value = "MySQL", comment = "目前只支持MySQL")
    public String type;

    @EntityFieldAnnotation(length = "50")
    public String ip;
    @EntityFieldAnnotation(text = "数据库端口", default_value = "3308")
    public int port;
    @EntityFieldAnnotation(length = "20")
    public String username;
    @EntityFieldAnnotation(length = "20")
    public String password;

    @EntityFieldAnnotation(length = "200", text = "备注", default_value = "")
    public String remark;
}