package com.lchml.webcat.ex;

/**
 * Created by lc on 11/11/17.
 */
public class WebcatStartException extends Exception {

    public WebcatStartException(String msg, Throwable e) {
        super(msg, e);
    }

    public WebcatStartException(String msg) {
        this(msg, null);
    }
}
