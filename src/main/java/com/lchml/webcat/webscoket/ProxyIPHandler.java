package com.lchml.webcat.webscoket;

import com.lchml.webcat.util.RequestUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;

/**
 * Created by lc on 17/7/13.
 */
public class ProxyIPHandler extends SimpleChannelInboundHandler<HttpRequest> {
    private static final Logger logger = LoggerFactory.getLogger(ProxyIPHandler.class);

    public static final String NAME = "proxyip";

    private boolean useProxy = false;

    public ProxyIPHandler(boolean useProxy) {
        super(false);
        this.useProxy = useProxy;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
        setRealRequestIp(request, ctx);
        ctx.fireChannelRead(request);
    }

    private void setRealRequestIp(HttpRequest request, ChannelHandlerContext ctx) {
        try {
            ChannelInfo ci = ChannelInfo.getChannelInfo(ctx.channel());
            if (ci == null) {
                return;
            }
            if (!StringUtils.isEmpty(ci.getClientIp())) {
                logger.debug("ip already set!, skip");
                return;
            }
            if (useProxy) {
                ci.setClientIp(RequestUtil.getRealIp(request, ctx));
            } else {
                InetSocketAddress addr = (InetSocketAddress) ctx.channel().remoteAddress();
                ci.setClientIp(addr.getAddress().getHostAddress());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

}
