package doys.framework.a1.sys;
import doys.framework.a2.base.BASE_ENTITY;
import doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import doys.framework.upgrade.db.enum1.EntityTableMatch;

@EntityTableAnnotation(databasePk = "sys", match = EntityTableMatch.strict)
@EntityIndexAnnotation(pk = "id", ux = { "name", "short_name" })
public class sys_tenant extends BASE_ENTITY {
    @EntityFieldAnnotation(length = "30", not_null = true, text = "数据库实例标识")
    public String instance_pk;
    @EntityFieldAnnotation(length = "10", text = "租户类型", comment = "F：正式租户；D：演示；T：试用；U：其它")
    public String type;

    @EntityFieldAnnotation(not_null = true)
    public int id;

    @EntityFieldAnnotation(length = "50", text = "商户名称")
    public String name;
    @EntityFieldAnnotation(length = "20", text = "商户简称")
    public String short_name;

    @EntityFieldAnnotation(text = "开户日期")
    public java.sql.Date bus_date;
    @EntityFieldAnnotation(text = "到期日期")
    public java.sql.Date exp_date;
    @EntityFieldAnnotation(length = "20", text = "客户经理")
    public String salesman;

    @EntityFieldAnnotation(length = "200", text = "备注")
    public String remark;
}
