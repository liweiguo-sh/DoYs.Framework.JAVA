package com.doys.aprint.dts;
import com.doys.framework.dts.parent.BASE_CRUD;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
@EntityTableAnnotation()
public class base_label extends BASE_CRUD {
    @EntityFieldAnnotation(length = "20")
    String label_file_name;
}
