/******************************************************************************
 * Copyright (C), 2020-2021, doys-next.com
 * @author David.Li
 * @version 1.0
 * @create_date 2021-05-28
 * 经过计算后的用户可访问菜单列表，根据user_pk直接获得可访问菜单列表(包括一级、二级等虚拟菜单)
 * 通过重置 sys_user.flag_menu_overdue = 1可以触发用户登陆时重新计算可访问菜单列表
 *****************************************************************************/
package doys.framework.a1.prefix;
import doys.framework.a2.base.BASE_ENTITY;
import doys.framework.upgrade.db.annotation.EntityFieldAnnotation;
import doys.framework.upgrade.db.annotation.EntityIndexAnnotation;
import doys.framework.upgrade.db.annotation.EntityTableAnnotation;
import doys.framework.upgrade.db.enum1.EntityTableMatch;

@EntityTableAnnotation(databasePk = "prefix", match = EntityTableMatch.strict)
@EntityIndexAnnotation(pk = "user_pk, menu_pk", ix = { "user_pk", "menu_pk" })
public class sys_user_menu extends BASE_ENTITY {
    @EntityFieldAnnotation(length = "20", not_null = true)
    public String user_pk;

    @EntityFieldAnnotation(length = "15", not_null = true)
    public String menu_pk;
}