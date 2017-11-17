package com.lchml.webcat.util;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.CharsetUtil;

/**
 * Created by lc on 11/14/17.
 */
public class ResponseUtil {

    public static void status(FullHttpResponse response, HttpResponseStatus status) {
        response.setStatus(status);
    }

    public static void content(FullHttpResponse response, String msg) {
        response.content().writeBytes(Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
    }

    public static void status(FullHttpResponse response, HttpResponseStatus status, String msg) {
        response.setStatus(status);
        response.content().writeBytes(Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
    }

    public static void missParam(FullHttpResponse response, String paramName) {
        status(response, HttpResponseStatus.BAD_REQUEST, String.format("miss parameter: %s", paramName));
    }

    public static void notSupportMehotd(FullHttpResponse response, String method) {
        status(response, HttpResponseStatus.BAD_REQUEST, String.format("not support method: %s", method));
    }

    public static void notSupportContentType(FullHttpResponse response, String contentType) {
        status(response, HttpResponseStatus.BAD_REQUEST, String.format("not support content-type: %s", contentType));
    }

    public static void missHeader(FullHttpResponse response, String header) {
        status(response, HttpResponseStatus.BAD_REQUEST, String.format("miss header: %s", header));
    }

    public static void addHeader(FullHttpResponse response, CharSequence name, Object value) {
        response.headers().add(name, value);
    }

    public static void cookie(FullHttpResponse response, Cookie... cookies) {
        response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookies));
    }

    public static void redirect(FullHttpResponse response, String redirect) {
        status(response, HttpResponseStatus.FOUND);
        addHeader(response, HttpHeaderNames.LOCATION, redirect);
    }

    public static void contentType(FullHttpResponse response, String contentType) {
        addHeader(response, HttpHeaderNames.CONTENT_TYPE, "application/json;charset=utf-8");
    }
}
