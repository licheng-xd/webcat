package com.lchml.webcat.webscoket;

import com.lchml.webcat.config.WebcatWsConf;
import com.lchml.webcat.util.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lc on 17/5/5.
 */
@Sharable
@Component
public class WsPacketDispatcher extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(WsPacketDispatcher.class);

    public static final String NAME = "packetHandler";

    @Autowired
    private WsRequestInvoker wsRequestInvoker;

    @Autowired
    private WebcatWsConf webcatWsConf;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof WebSocketFrame) {
                WebSocketFrame frame = (WebSocketFrame) msg;
                if (frame instanceof BinaryWebSocketFrame) {
                    ByteBuf byteBuf = frame.content();
                    byte[] bytes = new byte[byteBuf.readableBytes()];
                    byteBuf.readBytes(bytes);
                    handleWebsocket(ctx, MsgpackUtil.upackMap(bytes));
                } else if (frame instanceof TextWebSocketFrame) {
                    Map<String, Object> data = JsonUtil.json2Map(((TextWebSocketFrame) frame).text());
                    handleWebsocket(ctx, data);
                } else {
                    // ws protocol auto handle
                }
            } else if (msg instanceof FullHttpRequest) {
                NettyHttpUtil.errorResponse(ctx, (FullHttpRequest) msg, HttpResponseStatus.FORBIDDEN);
            } else {
                logger.warn("unknow request protocol msg, close channel");
                ctx.channel().close();
            }
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            NettyWsUtil.badRequest(ctx.channel());
            ctx.channel().close();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void handleWebsocket(ChannelHandlerContext ctx, Map<String, Object> data) {
        WebcatLog.init();
        long starttime = System.currentTimeMillis();
        WebcatLog.setStarttime(starttime);
        if (data == null) {
            logger.warn("request data invalid, close channel!");
            NettyWsUtil.badRequest(ctx.channel());
            ctx.channel().close();
            return;
        }
        String path = MapUtil.getString(data, WsRequestKey.PATH);
        WebcatLog.setPath(path);
        int mid = MapUtil.getIntValue(data, WsRequestKey.MID, 0);
        int version = MapUtil.getIntValue(data, WsRequestKey.VERSION, 0);
        Map<String, Object> params = MapUtil.getMap(data, WsRequestKey.PARAMS, new HashMap<String, Object>());
        ChannelInfo ci = ChannelInfo.getChannelInfo(ctx.channel());
        WebcatLog.setIp(ci.getClientIp());
        WsContext wsContext = new WsContext(path, ci, mid, version, ctx.channel(), params);
        WsResponse response = WsResponse.create(wsContext);
        try {
            if (wsRequestInvoker.validPath(path)) {
                wsRequestInvoker.invoke(wsContext, response);
            } else {
                response.code(WsCode.INVALID_PATH);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            response.code(WsCode.INTERNAL_ERROR);
        } finally {
            NettyWsUtil.sendWsResponse(ctx.channel(), response);
            WebcatLog.setRetcode(response.getCode());
            WebcatLog.setSpendtime(System.currentTimeMillis() - starttime);
            if (webcatWsConf.isLogEnable()) {
                WebcatLog.info();
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

}
