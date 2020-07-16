package com.doys.framework.dts.parent;
import com.doys.framework.database.DBFactory;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.enum1.EntityFieldType;
public class BASE_ENTITY_AUTO extends BASE_ENTITY {
    @EntityFieldAnnotation(type = EntityFieldType.INT, auto = true, not_null = true, comment = "主键")
    public long id;
    // ----------------------------------------------------
    @Override
    public void init(DBFactory dbBus) {
        isAuto = true;
        super.init(dbBus);
    }
    // ------------------------------------------------------------------------
    public long getId() throws Exception {
        if (isNewRecord) {
            this.id = dbBus.getLong("SELECT @@identity");
        }
        return this.id;
    }
}