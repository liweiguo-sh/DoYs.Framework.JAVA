package com.doys.aprint.dts.base;
import com.doys.framework.dts.base.BASE_ENTITY_CRUD;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import com.doys.framework.upgrade.db.enum1.EntityFieldType;
import com.doys.framework.upgrade.db.enum1.EntityTableMatch;

@EntityTableAnnotation(match = EntityTableMatch.strict)
@EntityIndexAnnotation(pk = "id", ux = "label_id,name", ix = { "label_id" })
public class base_label_variable extends BASE_ENTITY_CRUD {
    public int label_id;

    @EntityFieldAnnotation(text = "变量名称", not_null = true, length = "30")
    public String name;
    @EntityFieldAnnotation(text = "类型", not_null = true, length = "20", comment = "fixed,string,date,ref,seq,ai,js")
    public String type;

    // -- 引用 ----------------------------------------------
    @EntityFieldAnnotation(text = "引用来源", length = "20", comment = "customer、product")
    public String quote_from;
    @EntityFieldAnnotation(text = "引用名称", length = "30")
    public String quote_name;

    // -- 变量 ----------------------------------------------
    @EntityFieldAnnotation(text = "变量值", length = "100", comment = "当前值，也是占位符")
    public String value;
    @EntityFieldAnnotation(text = "变量长度")
    public int value_len;
    @EntityFieldAnnotation(text = "格式化结果", length = "50", comment = "变量值格式化之后的结果")
    public String value_format;

    // -- 日期 ----------------------------------------------
    @EntityFieldAnnotation(text = "日期格式化", length = "20")
    public String rule_date_format;
    @EntityFieldAnnotation(text = "日期偏移量")
    public int rule_date_offset;
    @EntityFieldAnnotation(text = "日期偏移量单位", length = "10", comment = "Day：日，Week：周，Month：月，Year：年")
    public String rule_date_offset_unit;

    // -- 序列 ----------------------------------------------
    @EntityFieldAnnotation(text = "序列归零条件字段", length = "100")
    public String seq_fields;

    // -- 选项 ----------------------------------------------
    @EntityFieldAnnotation(type = EntityFieldType.TINYINT, text = "允许打印前修改")
    public int flag_manual_modify = 1;

    // -- 其它 ----------------------------------------------
    @EntityFieldAnnotation(text = "序号", default_value = "900")
    public int sequence = 1;
    @EntityFieldAnnotation(text = "备注", length = "100")
    public String remark;
}