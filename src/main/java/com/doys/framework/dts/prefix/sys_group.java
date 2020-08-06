package com.doys.framework.dts.prefix;
import com.doys.framework.dts.base.BASE_ENTITY_CRUD;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import com.doys.framework.upgrade.db.enum1.EntityFieldType;
import com.doys.framework.upgrade.db.enum1.EntityTableMatch;

@EntityTableAnnotation(databasePk = "prefix", match = EntityTableMatch.strict)
@EntityIndexAnnotation(pk = "id", ux = { "pk" })
public class sys_group extends BASE_ENTITY_CRUD {
    @EntityFieldAnnotation(length = "20")
    public String pk;

    @EntityFieldAnnotation(length = "20")
    public String name;

    @EntityFieldAnnotation(type = EntityFieldType.TINYINT, text = "内置用户组", comment = "内置用户组不允许删除")
    public int flag_built_in = 0;

    @EntityFieldAnnotation(length = "100")
    public String remark;
}