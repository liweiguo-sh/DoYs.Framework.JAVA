package com.doys.aprint.dts.base;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import com.doys.framework.upgrade.db.enum1.EntityFieldType;
import com.doys.framework.upgrade.db.enum1.EntityTableMatch;
@EntityTableAnnotation(match = EntityTableMatch.appand)
@EntityIndexAnnotation(pk = "id", ux = "label_id,name", ix = { "label_id" })
public class base_label_variable {
    public int label_id;
    @EntityFieldAnnotation(auto = true)
    public int id;

    @EntityFieldAnnotation(text = "变量名称", not_null = true, length = "30")
    public String name;

    @EntityFieldAnnotation(type = EntityFieldType.TINYINT, text = "允许打印前修改")
    public int flag_manual_modify = 1;
}