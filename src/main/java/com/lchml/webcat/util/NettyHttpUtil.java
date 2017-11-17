package com.lchml.webcat.util;

import com.google.common.base.Strings;
import com.lchml.webcat.http.HttpRequestData;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NettyHttpUtil {

    public static void errorResponse(ChannelHandlerContext ctx, FullHttpRequest req, HttpResponseStatus status) {
        errorResponse(ctx, req, status, null);
    }

    public static void errorResponse(ChannelHandlerContext ctx, FullHttpRequest req, HttpResponseStatus status, String msg) {
        FullHttpResponse err = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN);
        try {
            if (!Strings.isNullOrEmpty(msg)) {
                err.content().writeBytes(Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
            }
            if (status != HttpResponseStatus.CONTINUE) {
                HttpUtil.setContentLength(err, err.content().readableBytes());
            }
            if (ctx.channel().isActive()) {
                ChannelFuture f = ctx.channel().writeAndFlush(err);
                if (!HttpUtil.isKeepAlive(req) || status == HttpResponseStatus.OK) {
                    f.addListener(ChannelFutureListener.CLOSE);
                }
            }
        } finally {
            if (err.content().refCnt() > 0) {
                err.content().release();
            }
        }

    }

    public static void succResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        try {
            if (res.status() != HttpResponseStatus.CONTINUE) {
                HttpUtil.setContentLength(res, res.content().readableBytes());
            }
            if (ctx.channel().isActive()) {
                ChannelFuture f = ctx.channel().writeAndFlush(res);
                if (!HttpUtil.isKeepAlive(req) || res.status() == HttpResponseStatus.OK) {
                    f.addListener(ChannelFutureListener.CLOSE);
                }
            }
        } finally {
            if (res.content().refCnt() > 0) {
                res.content().release();
            }
        }
    }



}
