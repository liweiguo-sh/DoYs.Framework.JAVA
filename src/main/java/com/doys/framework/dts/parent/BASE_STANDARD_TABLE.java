package com.doys.framework.dts.parent;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
public class BASE_STANDARD_TABLE {
    @EntityFieldAnnotation(auto = true, not_null = true, comment = "主键")
    public long id;
}