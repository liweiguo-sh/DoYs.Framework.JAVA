package doys.framework.core.ex;
public class SessionNotLoginException extends Exception {
    public SessionNotLoginException() {
        super("not login");
    }
}