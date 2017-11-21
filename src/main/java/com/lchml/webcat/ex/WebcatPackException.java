package com.lchml.webcat.ex;

/**
 * Created by lc on 11/20/17.
 */
public class WebcatPackException extends RuntimeException  {

    public WebcatPackException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public WebcatPackException(String msg) {
        super(msg);
    }
}
