package doys.framework.core.ex;
public class UnexpectedException extends Exception {
    public UnexpectedException() {
        super("系统遇到非预期的意外错误，请检查。");
    }
    public UnexpectedException(String strException) {
        super("系统遇到非预期的意外错误：" + strException);
    }
}