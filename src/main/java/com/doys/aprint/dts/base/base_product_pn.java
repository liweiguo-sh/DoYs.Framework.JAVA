package com.doys.aprint.dts.base;
import com.doys.framework.dts.base.BASE_ENTITY_AUTO;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import com.doys.framework.upgrade.db.enum1.EntityTableMatch;

@EntityTableAnnotation(match = EntityTableMatch.strict, remark = "鉴于是通用程序，涉及到第三方接口数据，所以不设置ux索引，避免数据接口失败")
@EntityIndexAnnotation(pk = "id", ix = { "product_id,pn", "product_id", "ref_uk" })
public class base_product_pn extends BASE_ENTITY_AUTO {
    @EntityFieldAnnotation(length = "20", comment = "第三方系统的唯一标识, 一般用于接口")
    public String ref_uk;

    @EntityFieldAnnotation(text = "产品ID", not_null = true, default_value = "0", comment = "产品ID")
    public int product_id;

    @EntityFieldAnnotation(text = "料号", length = "30")
    public String pn;
    @EntityFieldAnnotation(text = "规格", length = "50")
    public String spec;
    @EntityFieldAnnotation(text = "型号", length = "50")
    public String model;

    @EntityFieldAnnotation(text = "备注", length = "100")
    public String remark;
}