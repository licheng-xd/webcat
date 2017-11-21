package com.lchml.webcat.webscoket;

import com.lchml.webcat.common.WebcatServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

/**
 * Created by lc on 11/17/17.
 */
@Component
public class WebcatWsServer implements WebcatServer {
    private final static Logger logger = LoggerFactory.getLogger(WebcatWsServer.class);

    @Resource
    private AutowireCapableBeanFactory beanFactory;

    private int port;

    private int bossThread;

    private int workerThread;

    private ChannelConnectListener channelConnectListener;

    private ChannelDisconnectListener channelDisconnectListener;

    public WebcatWsServer() {
        bossThread = 1;
        workerThread = Runtime.getRuntime().availableProcessors() * 4;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setBossThread(int bossThread) {
        this.bossThread = bossThread;
    }

    public void setWorkerThread(int workerThread) {
        this.workerThread = workerThread;
    }

    public ChannelConnectListener getChannelConnectListener() {
        return channelConnectListener;
    }

    public void setChannelConnectListener(
        ChannelConnectListener channelConnectListener) {
        this.channelConnectListener = channelConnectListener;
    }

    public ChannelDisconnectListener getChannelDisconnectListener() {
        return channelDisconnectListener;
    }

    public void setChannelDisconnectListener(
        ChannelDisconnectListener channelDisconnectListener) {
        this.channelDisconnectListener = channelDisconnectListener;
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(bossThread);
        EventLoopGroup workerGroup = new NioEventLoopGroup(workerThread);
        try {
            ServerBootstrap b = new ServerBootstrap();
            WsServerInitializer initializer = beanFactory.getBean(WsServerInitializer.class);
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(initializer);
            ChannelFuture f = b.bind(new InetSocketAddress(port));
            f.addListener(new FutureListener<Void>() {
                @Override public void operationComplete(Future future)
                    throws Exception {
                    if (future.isSuccess()) {
                        logger.info("webcat ws server started listening at {} ", port);
                    } else {
                        logger.error("webcat ws server started fail! port={}, cause={}",
                            port, future.cause());
                    }
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
