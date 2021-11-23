package doys.framework.core.entity;
import java.util.ArrayList;

public class RestError extends ArrayList<String> {
    public String innerCode = "";       // -- 内部错误代码 --
    public String code = "9999";            // -- 错误代码 --

    public String toString() {
        StringBuilder sb = new StringBuilder(this.size());
        for (int i = 0; i < this.size(); i++) {
            sb.append(this.get(i));
        }
        return sb.toString();
    }
}