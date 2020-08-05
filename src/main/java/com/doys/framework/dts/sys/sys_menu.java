package com.doys.framework.dts.sys;
import com.doys.framework.dts.base.BASE_ENTITY_AUTO;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import com.doys.framework.upgrade.db.enum1.EntityFieldType;
import com.doys.framework.upgrade.db.enum1.EntityTableMatch;

@EntityTableAnnotation(databasePk = "sys", match = EntityTableMatch.appand)
@EntityIndexAnnotation(pk = "id", ux = { "pk" })
public class sys_menu extends BASE_ENTITY_AUTO {
    @EntityFieldAnnotation(length = "15")
    public String pk;

    @EntityFieldAnnotation(text = "禁用标志")
    int flag_disabled = 0;

    @EntityFieldAnnotation(type = EntityFieldType.TINYINT, text = "允许重复打开")
    public int allow_multi = 0;

    @EntityFieldAnnotation(type = EntityFieldType.TINYINT, text = "最大化窗口")
    public int flag_maximized = 0;

    @EntityFieldAnnotation(length = "20")
    public String sequences;
}
