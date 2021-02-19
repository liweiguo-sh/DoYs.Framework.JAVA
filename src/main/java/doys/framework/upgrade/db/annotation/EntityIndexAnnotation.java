package doys.framework.upgrade.db.annotation;

import java.lang.annotation.*;

/**
 * 表索引注解<br>
 * <b>pk:</b> 主键<br>
 * <b>ux:</b> 唯一索引数组<br>
 * <b>ix:</b> 普通索引数组<br>
 *
 * @author volant
 * @since 2020-03-08
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EntityIndexAnnotation {
    /**
     * 主键，单字段或多字段 <br>
     * 示例："id" 或者 "field1,field2"
     */
    String pk() default "";

    /**
     * 唯一索引数组，单字段索引或复合索引 <br>
     * 示例：{"field1", "field2, field3, field4"}
     */
    String[] ux() default {};

    /**
     * 普通索引数组，单字段索引或复合索引 <br>
     * 示例：{"field1", "field2, field3, field4"}
     */
    String[] ix() default {};
}