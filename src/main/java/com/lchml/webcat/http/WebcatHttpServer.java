package com.lchml.webcat.http;

import com.lchml.webcat.ex.WebcatStartException;
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
 * Created by lc on 11/11/17.
 */
@Component
public class WebcatHttpServer implements HttpServer {
    private static final Logger logger = LoggerFactory.getLogger(
        WebcatHttpServer.class);

    @Resource
    private AutowireCapableBeanFactory beanFactory;

    private int port;

    private int bossThread;

    private int workerThread;

    public WebcatHttpServer() {
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

    public void start() throws WebcatStartException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(bossThread);
        EventLoopGroup workerGroup = new NioEventLoopGroup(workerThread);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(beanFactory.getBean(HttpServerInitializer.class));
            if (port == 0) {
                throw new WebcatStartException("port can't be 0, abord http server.");
            }
            if (bossThread == 0 || workerThread == 0) {
                throw new WebcatStartException("bossThread and workerThread can't be 0, abord http server.");
            }

            ChannelFuture f = b.bind(new InetSocketAddress(port));
            f.addListener(new FutureListener<Void>() {
                @Override public void operationComplete(Future future)
                    throws Exception {
                    if (future.isSuccess()) {
                        logger.info("webcat http server started listening at {}", port);
                    } else {
                        logger.error("webcat http server started fail! port={}, cause={}", port, future.cause());
                    }
                }
            });
        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            throw new WebcatStartException("fatal error where start webcat http server.", e);
        }
    }
}
