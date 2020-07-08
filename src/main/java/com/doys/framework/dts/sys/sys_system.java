package com.doys.framework.dts.sys;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
@EntityTableAnnotation(databasePk = "sys")
@EntityIndexAnnotation(pk = "pk")
public class sys_system {
    @EntityFieldAnnotation(text = "主键", length = "3", not_null = true)
    String pk;

    @EntityFieldAnnotation(text = "名称", length = "20")
    String name;

    @EntityFieldAnnotation(text = "中文名称", length = "20")
    String text;

    @EntityFieldAnnotation(text = "禁用标志", not_null = true, default_value = "0")
    int flag_disabled;

    @EntityFieldAnnotation(text = "序号", default_value = "999")
    int sequence;
}
