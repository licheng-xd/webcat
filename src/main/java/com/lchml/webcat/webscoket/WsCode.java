package com.lchml.webcat.webscoket;

/**
 * Created by lc on 11/20/17.
 */
public enum WsCode {

    OK(200, "OK"),

    NO_PATH(300, "path can't be empty"),
    VERSION_NOT_SUPPORT(301, "version not support"),
    MISS_PARAM(302, "miss param"),
    INVALID_PATH(303, "invalid path"),
    BAD_REQUEST(304, "bad request"),

    INTERNAL_ERROR(500, "internal error"),
    ;

    private int code;
    private String msg;

    WsCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int code() {
        return code;
    }

    public String msg() {
        return msg;
    }
}
