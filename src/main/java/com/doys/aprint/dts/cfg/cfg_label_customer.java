package com.doys.aprint.dts.cfg;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import com.doys.framework.upgrade.db.enum1.EntityTableMatch;

@EntityTableAnnotation(match = EntityTableMatch.strict)
@EntityIndexAnnotation(pk = "label_id,customer_id", ix = { "label_id", "customer_id" })
public class cfg_label_customer {
    @EntityFieldAnnotation(not_null = true)
    private int label_id;

    @EntityFieldAnnotation(not_null = true)
    private int customer_id;
}