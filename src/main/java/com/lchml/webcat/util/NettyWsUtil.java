package com.lchml.webcat.util;

import com.lchml.webcat.webscoket.WsCode;
import com.lchml.webcat.webscoket.WsContext;
import com.lchml.webcat.webscoket.WsResponse;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lc on 11/20/17.
 */
public class NettyWsUtil {
    private static final Logger logger = LoggerFactory.getLogger(NettyWsUtil.class);

    public static void noPath(WsContext ctx) {
        sendWsResponse(ctx.getChannel(), WsResponse.create(ctx).code(WsCode.NO_PATH));
    }

    public static void badRequest(Channel channel) {
        sendWsResponse(channel, WsResponse.create().code(WsCode.BAD_REQUEST));
    }

    public static void versionNotSupport(WsContext ctx) {
        sendWsResponse(ctx.getChannel(), WsResponse.create(ctx).code(WsCode.VERSION_NOT_SUPPORT));
    }

    public static void invalidPath(WsContext ctx) {
        sendWsResponse(ctx.getChannel(), WsResponse.create(ctx).code(WsCode.INVALID_PATH));
    }

    public static void internalError(WsContext ctx) {
        sendWsResponse(ctx.getChannel(), WsResponse.create(ctx).code(WsCode.INTERNAL_ERROR));
    }

    public static void internalError(Channel channel) {
        sendWsResponse(channel, WsResponse.create().code(WsCode.INTERNAL_ERROR));
    }

    public static void sendWsResponse(Channel channel, WsResponse response) {
        if (channel.isActive()) {
            channel.writeAndFlush(new TextWebSocketFrame((JsonUtil.toJson(response))));
        } else {
            logger.warn("channel[{}] is not active, can't write data.", channel.id().toString());
        }
    }

}
