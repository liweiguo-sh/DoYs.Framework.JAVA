package com.doys.aprint.dts.base;
import com.doys.framework.dts.parent.BASE_ENTITY_AUTO;
import com.doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import com.doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import com.doys.framework.upgrade.db.enum1.EntityTableMatch;

@EntityTableAnnotation(match = EntityTableMatch.strict)
@EntityIndexAnnotation(pk = "id", ux = "node_key", ix = { "id_parent", "id1", "id2", "id3", "id4", "id5" })
public class base_category extends BASE_ENTITY_AUTO {
    @EntityFieldAnnotation(not_null = true)
    public int id_parent;

    @EntityFieldAnnotation(length = "18")
    public String node_key;

    @EntityFieldAnnotation(text = "名称", length = "20")
    public String name;
    @EntityFieldAnnotation(length = "100")
    public String fullname;

    @EntityFieldAnnotation(comment = "是否末级节点")
    public int is_leaf = 1;

    public int id1;
    public String name1;
    public int id2;
    public String name2;
    public int id3;
    public String name3;
    public int id4;
    public String name4;
    public int id5;
    public String name5;

    public int sequence = 900;
    public String sequences;

    @EntityFieldAnnotation(text = "备注", length = "100")
    public String remark;
}