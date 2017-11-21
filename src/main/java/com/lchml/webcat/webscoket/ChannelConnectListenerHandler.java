package com.lchml.webcat.webscoket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lc on 11/21/17.
 */
public class ChannelConnectListenerHandler extends SimpleChannelInboundHandler<HttpRequest> {
    private static final Logger logger = LoggerFactory.getLogger(ChannelConnectListenerHandler.class);

    public static final String NAME = "channelStateListener";

    private ChannelConnectListener listener;

    public ChannelConnectListenerHandler(ChannelConnectListener listener) {
        super(false);
        this.listener = listener;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
        callback(ctx);
        ctx.fireChannelRead(msg);
    }

    private void callback(ChannelHandlerContext ctx) {
        try {
            listener.connect(ChannelInfo.getChannelInfo(ctx.channel()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
