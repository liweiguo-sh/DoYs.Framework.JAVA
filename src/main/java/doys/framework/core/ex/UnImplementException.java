package doys.framework.core.ex;
public class UnImplementException extends Exception {
    public UnImplementException() {
        super("暂未实现的功能，请检查。");
    }
    public UnImplementException(String strFunction) {
        super("暂未实现的功能 (" + strFunction + ")，请检查。");
    }
}