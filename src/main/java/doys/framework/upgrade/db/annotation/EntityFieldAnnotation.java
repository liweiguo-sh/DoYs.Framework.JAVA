package doys.framework.upgrade.db.annotation;
import doys.framework.upgrade.db.enumeration.EntityFieldType;

import java.lang.annotation.*;

/**
 * 表字段注解<br>
 * <b>type: </b>字段类型<br>
 * <b>auto: </b>是否自增量<br>
 * <b>length: </b>字段长度<br>
 * <b>not_null: </b>不允许为空<br>
 * <b>default_value: </b>默认值<br>
 * <b>text: </b>中文名称<br>
 * <b>comment: </b>字段注释<br>
 *
 * @author volant
 * @since 2020-03-08
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface EntityFieldAnnotation {
    public static String undefined = "__undefined__";       // -- 数据库字段默认值：未定义 --
    public static String now = "CURRENT_TIMESTAMP";         // -- 数据库字段默认值：now，格式：yyyy-MM-dd HH:mm:ss --
    public static String nul = "NULL";                      // -- 数据库字段默认值：null --

    /**
     * 字段类型
     */
    EntityFieldType type() default EntityFieldType.UNKNOWN;

    /**
     * 是否自增量字段
     */
    boolean auto() default false;

    /**
     * 字段长度，适用于string、float、double。float、double示例：length = "10,3"
     */
    String length() default "";

    /**
     * 不允许为空
     */
    boolean not_null() default false;

    /**
     * 默认值
     * 数据库时间：CURRENT_TIMESTAMP
     */
    String default_value() default undefined;

    /**
     * 字段中文名称
     */
    String text() default "";

    /**
     * 字段注释
     */
    String comment() default "";
}