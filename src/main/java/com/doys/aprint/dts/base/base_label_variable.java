package com.doys.aprint.dts.base;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import com.doys.framework.upgrade.db.enum1.EntityTableMatch;
@EntityTableAnnotation(match = EntityTableMatch.appand)
@EntityIndexAnnotation(pk = "id", ux = "label_id,name", ix = { "label_id" })
public class base_label_variable {
    public int label_id;
    @EntityFieldAnnotation(auto = true)
    public int id;

    public String name;

    @EntityFieldAnnotation(text = "隐藏变量", not_null = true, comment = "程序生成的变量，非用户手工定义")
    public int hidden = 0;
}