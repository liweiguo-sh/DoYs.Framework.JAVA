package com.doys.framework.common.entity;
public class EntityTableField {
    public String name;
    public String datatype = "varchar";
    private int length = 50;

    public EntityTableField(String name) {
        this.name = name;
    }
}
