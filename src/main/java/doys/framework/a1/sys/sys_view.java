package doys.framework.a1.sys;
import doys.framework.a2.base.BASE_ENTITY_AUTO;
import doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import doys.framework.upgrade.db.enum1.EntityFieldType;

@EntityTableAnnotation(databasePk = "sys")
@EntityIndexAnnotation(pk = "id", ux = { "pk" })
public class sys_view extends BASE_ENTITY_AUTO {
    @EntityFieldAnnotation(length = "30")
    public String pk;

    @EntityFieldAnnotation(type = EntityFieldType.TINYINT)
    public int allow_addnew = 0;
    @EntityFieldAnnotation(type = EntityFieldType.TINYINT)
    public int allow_update = 0;
    @EntityFieldAnnotation(type = EntityFieldType.TINYINT)
    public int allow_delete = 0;
    @EntityFieldAnnotation(type = EntityFieldType.TINYINT)
    public int allow_copy = 0;

    @EntityFieldAnnotation(text = "导航树宽度")
    public int tree_width;

    @EntityFieldAnnotation(text = "显示编辑列", type = EntityFieldType.TINYINT)
    public int show_detail = 1;
    @EntityFieldAnnotation(text = "显示选择列", type = EntityFieldType.TINYINT)
    public int show_select = 0;
    @EntityFieldAnnotation(text = "显示单选列", type = EntityFieldType.TINYINT)
    public int show_single = 0;
    @EntityFieldAnnotation(text = "显示删除列", type = EntityFieldType.TINYINT)
    public int show_delete = 0;
}
