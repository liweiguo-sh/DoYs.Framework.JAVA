package com.doys.aprint.dts.cfg;
import com.doys.framework.dts.parent.BASE_ENTITY;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import com.doys.framework.upgrade.db.enum1.EntityFieldType;
import com.doys.framework.upgrade.db.enum1.EntityTableMatch;

@EntityTableAnnotation(match = EntityTableMatch.strict)
@EntityIndexAnnotation(pk = "pk")
public class cfg_jdbc extends BASE_ENTITY {
    @EntityFieldAnnotation(length = "30")
    public String pk;

    @EntityFieldAnnotation(length = "200")
    public String url;
    @EntityFieldAnnotation(length = "50", comment = "driverClassName")
    public String driver;

    @EntityFieldAnnotation(length = "20")
    public String username;
    @EntityFieldAnnotation(length = "20")
    public String password;

    @EntityFieldAnnotation(length = "30")
    public String tablename;
    @EntityFieldAnnotation(length = "30")
    public String fieldname;

    @EntityFieldAnnotation(text = "启用标志", type = EntityFieldType.TINYINT)
    public boolean flag_active = false;
}