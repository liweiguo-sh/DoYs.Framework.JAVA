package com.doys.aprint.dts.aid;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
@EntityTableAnnotation
@EntityIndexAnnotation(pk = "scope,sn_pk")
public class aid_serial {
    @EntityFieldAnnotation(length = "30", text = "序列所属范围", comment = "示例：office_id，warehouse_id等")
    public String scope;
    @EntityFieldAnnotation(length = "30")
    public String sn_pk;

    public int year;
    public int month;
    public int day;

    @EntityFieldAnnotation(text = "当前最大值", comment = "已使用的最大值，新值需要在当前值基础上加1")
    public int value;
}