package doys.framework.upgrade.db.enumeration;

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
     * decimal
     */
    DECIMAL,

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