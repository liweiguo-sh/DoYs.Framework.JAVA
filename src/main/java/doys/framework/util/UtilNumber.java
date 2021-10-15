package doys.framework.util;
import java.util.regex.Pattern;

public class UtilNumber {
    public static boolean isInt(String strInt) {
        String pattern = "[+-]?[0-9]+?";
        return Pattern.matches(pattern, strInt);
    }
}