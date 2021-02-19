package ems.dts;
import doys.framework.a2.base.BASE_ENTITY_AUTO;
import doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import doys.framework.upgrade.db.annotation.EntityTableAnnotation;

@EntityTableAnnotation
public class core_water_alarm extends BASE_ENTITY_AUTO {
    @EntityFieldAnnotation(text = "水表ID", not_null = true)
    int water_meter_id;

    @EntityFieldAnnotation(text = "报警时间", default_value = "CURRENT_TIMESTAMP")
    java.util.Date bus_date;

    @EntityFieldAnnotation(text = "类型", length = "20")
    String type;

    @EntityFieldAnnotation(text = "说明", length = "250")
    String description;
}