package doys.framework.core.ex;
public class SessionTimeoutException extends Exception {
    public SessionTimeoutException() {
        super("session timeout");
    }
}