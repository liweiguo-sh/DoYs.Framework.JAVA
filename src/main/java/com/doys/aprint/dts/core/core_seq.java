package com.doys.aprint.dts.core;
import com.doys.framework.dts.base.BASE_ENTITY_AUTO;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import com.doys.framework.upgrade.db.enum1.EntityTableMatch;

@EntityTableAnnotation(match = EntityTableMatch.strict)
@EntityIndexAnnotation(pk = "id", ux = { "label_id,var_name,prefix_value" })
public class core_seq extends BASE_ENTITY_AUTO {
    public int label_id;
    @EntityFieldAnnotation(text = "变量名称", length = "30")
    public String var_name;
    @EntityFieldAnnotation(text = "序列条件值", length = "100")
    public String prefix_value;

    @EntityFieldAnnotation(text = "序列值", length = "20", comment = "当前可用的最小值")
    public String seq_value;
}