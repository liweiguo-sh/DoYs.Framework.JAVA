package doys.framework.a0;
public class Const {
    public static String ok = "ok";

    public static String CHAR1 = "\1";        // -- 分隔符1 --
    public static String CHAR2 = "\2";        // -- 分隔符2 --
    public static String CHAR3 = "\3";        // -- 分隔符3 --
    public static String CHAR4 = "\4";        // -- 分隔符4 --
    public static String CHAR5 = "\5";        // -- 分隔符5 --
    public static String CHAR6 = "\6";        // -- 分隔符6, 框架保留分隔符, 程序员禁用. --
    public static String CHAR7 = "\7";        // -- 分隔符7, 框架保留分隔符, 程序员禁用. --

    public static String dateFormat = "yyyy-MM-dd";                  // -- 默认日期格式 --
    public static String timeFormat = "HH:mm:ss";                    // -- 默认时间格式 --
    public static String datetimeFormat = "yyyy-MM-dd HH:mm:ss";     // -- 默认日期时间格式 --
    public static String longDtFormat = "yyyy-MM-dd HH:mm:ss.SSS";   // -- 毫秒日期时间格式 --

    public static int MAX_MENU_LEVEL = 4;       // -- 系统最大支持 3 级菜单(注：第一级菜单是子系统) --
    public static int MAX_PAGE_ROWS = 100;      // -- 视图每页数据行数 --
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    public static String ERR_TENANT_ID = "S01";
    public static String ERROR_TENANT_ID = "参数[tenantId]为空或不存在";

    public static String ERR_NO_TOKEN = "S05";
    public static String ERROR_NO_TOKEN = "参数[token]为空或不存在";
    public static String ERR_TIMEOUT = "S06";
    public static String ERROR_TIMEOUT = "token已过期";

    public static String ERR_USER_NAME = "S11";
    public static String ERROR_USER_NAME = "参数[username]为空或不存在";
    public static String ERR_APP_SECRET = "S12";
    public static String ERROR_APP_SECRET = "参数[app_secret]为空或不正确";


}
