package com.doys.aprint.dts.aid;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
@EntityTableAnnotation
@EntityIndexAnnotation(pk = "pk")
public class aid_serial_definition {
    @EntityFieldAnnotation(text = "序列标识", length = "30")
    public String pk;

    @EntityFieldAnnotation(length = "30", text = "序列名称")
    public String name;

    @EntityFieldAnnotation(length = "30", text = "序列规则", comment = "示例：T-{yy}{mm}-{5}")
    public String rule;

    @EntityFieldAnnotation(length = "100")
    public String remark;
}