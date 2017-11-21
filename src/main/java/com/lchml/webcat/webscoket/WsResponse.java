package com.lchml.webcat.webscoket;

/**
 * Created by lc on 11/20/17.
 */
public class WsResponse {
    private String path;

    private int mid;

    private int version;

    private int code;

    private String msg;

    private Object payload;

    private WsResponse() {}

    public static WsResponse create() {
        return new WsResponse();
    }

    public static WsResponse create(WsContext ctx) {
        return WsResponse.create().path(ctx.getPath()).mid(ctx.getMid()).version(ctx.getVersion());
    }

    public WsResponse path(String path) {
        this.path = path;
        return this;
    }

    public WsResponse mid(int mid) {
        this.mid = mid;
        return this;
    }

    public WsResponse version(int version) {
        this.version = version;
        return this;
    }

    public WsResponse code(WsCode code) {
        this.code = code.code();
        this.msg = code.msg();
        return this;
    }

    public int getCode() {
        return this.code;
    }

    public WsResponse msg(String msg) {
        this.msg = msg;
        return this;
    }

    public WsResponse payload(Object payload) {
        this.payload = payload;
        return this;
    }
}
