package com.doys.aprint.dts.base;
import com.doys.framework.dts.base.BASE_ENTITY_AUTO;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import com.doys.framework.upgrade.db.enum1.EntityTableMatch;

@EntityTableAnnotation(match = EntityTableMatch.strict)
@EntityIndexAnnotation(pk = "id", ux = "name", ix = { "customer_id", "ref_uk" })
public class base_product extends BASE_ENTITY_AUTO {
    @EntityFieldAnnotation(length = "20", comment = "第三方系统的唯一标识, 一般用于接口")
    public String ref_uk;

    @EntityFieldAnnotation(text = "客户ID", default_value = "0", comment = "默认客户ID")
    public int customer_id;

    @EntityFieldAnnotation(text = "产品代码", length = "20")
    public String code;
    @EntityFieldAnnotation(text = "名称", length = "50", not_null = true)
    public String name;

    @EntityFieldAnnotation(text = "默认品规", length = "50")
    public String pn_default;

    @EntityFieldAnnotation(text = "备注", length = "100")
    public String remark;
}