package doys.framework.upgrade.db.entity;
import doys.framework.upgrade.db.annotation.EntityFieldAnnotation;


public class SampleEntity extends BaseEntity {
    @EntityFieldAnnotation(length = "20", text = "姓名", default_value = "无名氏", comment = "学生姓名")
    private String name;

    //@EntityFieldAnnotation(length = "55", text = "制单人3", comment = "单据制作人的系统账户1")
    //public String creator;
    private boolean isOmoc;
}