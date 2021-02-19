package doys.framework.core.entity;
import java.util.ArrayList;

public class RestError extends ArrayList<String> {
    public String toString() {
        StringBuilder sb = new StringBuilder(this.size());
        for (int i = 0; i < this.size(); i++) {
            sb.append(this.get(i));
        }
        return sb.toString();
    }
}