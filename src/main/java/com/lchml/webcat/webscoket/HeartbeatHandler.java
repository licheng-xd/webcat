package com.lchml.webcat.webscoket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lc on 17/5/9.
 */
public class HeartbeatHandler extends IdleStateHandler {
    private static final Logger logger = LoggerFactory.getLogger(HeartbeatHandler.class);

    public static final String NAME = "heartbeat";

    public HeartbeatHandler(int readerIdleTimeSeconds){
        this(readerIdleTimeSeconds, 0, 0); //默认不检测 WRITER_IDLE 和 ALL_IDEL 事件
    }

    public HeartbeatHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
        super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
    }

    @Override
    public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e)
        throws Exception {
        super.channelIdle(ctx, e);
        if (e.state() == IdleState.READER_IDLE) {
            Channel c = ctx.channel();
            logger.debug("channel been close for idle time reached! {}. readerIdleTimeSeconds: {}", c, getReaderIdleTimeInMillis());
            c.close();
        }
    }
}
