package com.lchml.webcat.webscoket;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by lc on 17/5/8.
 */
@Sharable
@Component
public class WsChannelInitHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(WsChannelInitHandler.class);

    public static final String NAME = "channelInit";

    @Autowired
    private WebcatWsServer webcatWsServer;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        logger.debug("ChannelInitHandler Exception occur: {}, {}", cause.getMessage(), cause.getStackTrace());
        ctx.channel().close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {    //表示channel 建立连接
        ChannelInfo ci = new ChannelInfo();
        ci.init(ctx.channel());
        logger.debug("channel[{}] connected!", ctx.channel().id().toString());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {    //表示channel 断开连接
        ChannelInfo ci = ChannelInfo.getChannelInfo(ctx.channel());
        if (ci != null && webcatWsServer.getChannelDisconnectListener() != null) {
            webcatWsServer.getChannelDisconnectListener().disconnect(ci);
        }
        ctx.channel().close().sync();
        super.channelInactive(ctx);
    }

}
