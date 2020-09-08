package com.doys.aprint.dts.projects.huisu;
import com.doys.framework.dts.base.BASE_ENTITY;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import com.doys.framework.upgrade.db.enum1.EntityFieldType;
import com.doys.framework.upgrade.db.enum1.EntityTableMatch;

import java.util.Date;

@EntityTableAnnotation(match = EntityTableMatch.strict)
@EntityIndexAnnotation(pk = "id", ux = { "single", "batch" }, ix = { "batch" })
public class t_huisu_udi_order extends BASE_ENTITY {
    @EntityFieldAnnotation(text = "生产单ID", length = "20", type = EntityFieldType.STRING)
    public String id;
    @EntityFieldAnnotation(text = "生产单号", length = "20")
    public String single;
    @EntityFieldAnnotation(text = "生产批次", length = "20")
    public String batch;

    @EntityFieldAnnotation(text = "产品ID", length = "20")
    public String productId;
    @EntityFieldAnnotation(text = "产品名称", length = "50")
    public String name;

    @EntityFieldAnnotation(text = "计划生产数量")
    public int planNumber;
    @EntityFieldAnnotation(text = "已生产上传数量")
    public int doNumber;

    @EntityFieldAnnotation(text = "生产日期")
    public Date pdate;

    @EntityFieldAnnotation(text = "创建人", length = "20")
    public String cname;
    @EntityFieldAnnotation(text = "创建时间")
    public Date cdate;
    @EntityFieldAnnotation(text = "更信任", length = "20")
    public String uname;
    @EntityFieldAnnotation(text = "更新时间")
    public Date udate;

    @EntityFieldAnnotation(text = "生产单状态", comment = "0：新建 1：已生码 2：已生产 （本接口只查询状态为0生产单")
    public int state;
    @EntityFieldAnnotation(text = "生产单状态", type = EntityFieldType.TINYINT, comment = "0 正常 1已删除")
    public int isDel;
    @EntityFieldAnnotation(text = "是否新记录", type = EntityFieldType.TINYINT)
    public int isNewRecord;
}