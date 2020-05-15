package com.doys.framework.core.ex;
public class Unexpected extends Exception {
    public Unexpected() {
        super("系统遇到非预期的意外错误，请检查。");
    }
    public Unexpected(String strException) {
        super(strException);
    }
}