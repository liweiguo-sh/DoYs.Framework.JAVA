package com.doys.framework.upgrade.db.entity;

import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.enum1.EntityFieldType;

import java.util.Date;

public class BaseEntity {
    @EntityFieldAnnotation(type = EntityFieldType.LONG)
    public long id;

    @EntityFieldAnnotation(length = "30", text = "制单人", comment = "单据制作人的系统账户")
    public String creator;
    @EntityFieldAnnotation(text = "制单时间")
    public Date create_time;

    private String noPhysicalField1;
}