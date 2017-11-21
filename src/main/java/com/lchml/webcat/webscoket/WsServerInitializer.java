package com.lchml.webcat.webscoket;

import com.lchml.webcat.config.WebcatWsConf;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class WsServerInitializer extends ChannelInitializer<SocketChannel> {

    @Resource
    private AutowireCapableBeanFactory beanFactory;

    @Autowired
    private WebcatWsConf webcatWsConf;

    @Autowired
    private WebcatWsServer webcatWsServer;

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(HeartbeatHandler.NAME, new HeartbeatHandler(webcatWsConf.getHeartbeat()));
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(webcatWsConf.getMaxPayload()));
        pipeline.addLast(WsChannelInitHandler.NAME, beanFactory.getBean(WsChannelInitHandler.class));
        pipeline.addLast(ProxyIPHandler.NAME, new ProxyIPHandler(webcatWsConf.isUseProxy()));
        if (webcatWsServer.getChannelConnectListener() != null) {
            pipeline.addLast(
                ChannelConnectListenerHandler.NAME, new ChannelConnectListenerHandler(webcatWsServer.getChannelConnectListener()));
        }
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler(webcatWsConf.getWsPath(), null, true, webcatWsConf.getMaxPayload()));
        pipeline.addLast(
            WsPacketDispatcher.NAME, beanFactory.getBean(WsPacketDispatcher.class));
    }

}
