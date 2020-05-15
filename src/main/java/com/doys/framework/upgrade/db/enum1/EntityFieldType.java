package com.doys.framework.upgrade.db.enum1;

public enum EntityFieldType {
    UNKNOWN,
    /**
     * varchar
     */
    STRING,
    /**
     * int
     */
    INT,
    /**
     * boolean, tinyint
     */
    TINYINT,
    /**
     * bigint
     */
    LONG,
    /**
     * float
     */
    FLOAT,
    /**
     * double
     */
    DOUBLE,
    /**
     * java.util.Date, java.sql.Timestamp
     */
    DATETIME,
    /**
     * java.sql.Date
     */
    DATE,
    /**
     * java.sql.Time
     */
    TIME,
    TEXT
}