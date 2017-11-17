package com.lchml.webcat.http;

import com.lchml.webcat.config.WebcatConf;
import com.lchml.webcat.ex.WebcatException;
import com.lchml.webcat.util.NettyHttpUtil;
import com.lchml.webcat.util.RequestUtil;
import com.lchml.webcat.util.ResponseUtil;
import com.lchml.webcat.util.WebcatLog;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

/**
 * dispatcher
 *
 * Created by lc on 11/11/17.
 */
@Component
@ChannelHandler.Sharable
public class HttpRequestDispatcher extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestDispatcher.class);

    @Autowired
    private HttpRequestInvoker httpRequestInvoker;

    @Autowired
    private WebcatConf webcatConf;


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof FullHttpRequest) {
                handleHttpRequest(ctx, (FullHttpRequest) msg);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) throws IOException {
        WebcatLog.init();
        long starttime = System.currentTimeMillis();
        WebcatLog.setStarttime(starttime);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        try {
            String ip = RequestUtil.getRealIp(request, ctx);
            WebcatLog.setIp(ip);
            if (!request.decoderResult().isSuccess()) {
                ResponseUtil.status(response, HttpResponseStatus.BAD_REQUEST, "request decode failed.");
            } else {
                if (isSupport(request.method())) {
                    URI uri = URI.create(request.uri());
                    WebcatLog.setPath(uri.getPath());
                    invoke(uri.getPath(), request, response, ip);
                } else {
                    ResponseUtil.status(response, HttpResponseStatus.FORBIDDEN, "http method not support");
                }
            }
        } finally {
            NettyHttpUtil.succResponse(ctx, request, response);
            WebcatLog.setRetcode(response.status().code());
            WebcatLog.setSpendtime(System.currentTimeMillis() - starttime);
            if (webcatConf.isLogEnable()) {
                WebcatLog.info();
            }
        }

    }

    private void invoke(String path, FullHttpRequest request, FullHttpResponse response, String ip) {
        try {
            if (httpRequestInvoker.validPath(path)) {
                HttpRequestData data = RequestUtil.parseRequest(request);
                data.setIp(ip);
                httpRequestInvoker.invoke(path, data, request, response);
            } else {
                ResponseUtil.status(response, HttpResponseStatus.FORBIDDEN, "uri not exist.");
            }
        } catch (Exception e) {
            logger.error("webcat invoke error: " + e.getMessage(), e);
            ResponseUtil.status(response, HttpResponseStatus.INTERNAL_SERVER_ERROR, "webcat invoke error.");
        }
    }

    private boolean isSupport(HttpMethod method) {
        return method == HttpMethod.GET
            || method == HttpMethod.POST
            || method == HttpMethod.PUT
            || method == HttpMethod.HEAD
            || method == HttpMethod.DELETE;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(), cause);
        throw new WebcatException("netty handler exception.", cause);
    }

}
