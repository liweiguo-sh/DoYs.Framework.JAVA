/**
 * 数据库实体类注解<br>
 * <b>databasePk:</b> 库名称<br>
 * <b>name:</b> 表名称<br>
 * <b>text:</b> 中文名称<br>
 * <b>remark:</b> 备注说明<br>
 *
 * @author volant
 * @since 2020-03-08
 */
package doys.framework.upgrade.db.annotation;

import doys.framework.upgrade.db.enumeration.EntityTableMatch;

import java.lang.annotation.*;
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EntityTableAnnotation {
    /**
     * 数据库名称
     */
    String databasePk() default "prefix";
    /**
     * 表名称
     */
    String name() default "";

    /**
     * 表中文名称
     */
    String text() default "";

    /**
     * 表备注
     */
    String remark() default "";

    EntityTableMatch match() default EntityTableMatch.appand;
}