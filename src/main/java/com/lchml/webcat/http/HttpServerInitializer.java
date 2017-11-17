package com.lchml.webcat.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by lc on 11/11/17.
 */
@Component
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final int DEFAULT_MAX_PAYLOAD_LENGTH = 1048576;

    @Resource
    private AutowireCapableBeanFactory beanFactory;

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(DEFAULT_MAX_PAYLOAD_LENGTH));
        pipeline.addLast(beanFactory.getBean(HttpRequestDispatcher.class));
    }
}
