package com.lchml.webcat.ex;

/**
 * Created by lc on 11/11/17.
 */
public class WebcatException extends Exception {

    public WebcatException(String msg, Throwable e) {
        super(msg, e);
    }

    public WebcatException(String msg) {
        this(msg, null);
    }
}
