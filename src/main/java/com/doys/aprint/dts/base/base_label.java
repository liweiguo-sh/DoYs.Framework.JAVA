package com.doys.aprint.dts.base;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import com.doys.framework.upgrade.db.enum1.EntityFieldType;
import com.doys.framework.upgrade.db.enum1.EntityTableMatch;
@EntityTableAnnotation(match = EntityTableMatch.appand)
@EntityIndexAnnotation(pk = "id", ix = { "product_id" })
public class base_label {
    @EntityFieldAnnotation(text = "分类ID")
    int category_id = 0;

    @EntityFieldAnnotation(length = "20")
    String label_file_name;

    // -- JavaScript --------------------------------------
    @EntityFieldAnnotation(text = "引用值变化后脚本", type = EntityFieldType.TEXT)
    public String js_after_ref_change = "";

    @EntityFieldAnnotation(text = "用户界面输入后脚本", type = EntityFieldType.TEXT)
    public String js_after_input = "";
}