package doys.framework.upgrade.db.enumeration;
public enum EntityTableMatch {
    /**
     * 精确匹配，多余字段自动删除
     */
    strict,

    /**
     * 追加，只追加新字段
     */
    appand
}