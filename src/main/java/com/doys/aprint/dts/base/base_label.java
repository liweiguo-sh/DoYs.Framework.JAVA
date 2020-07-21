package com.doys.aprint.dts.base;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
@EntityTableAnnotation()
@EntityIndexAnnotation(pk = "id", ix = { "product_id" })
public class base_label {
    @EntityFieldAnnotation(text = "分类ID")
    int category_id = 0;

    @EntityFieldAnnotation(length = "20")
    String label_file_name;
}