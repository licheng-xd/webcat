package com.lchml.webcat.http;

import com.lchml.webcat.config.WebcatHttpConf;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * Created by lc on 11/11/17.
 */
@Component
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Autowired
    private WebcatHttpConf webcatHttpConf;

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(webcatHttpConf.getMaxPayload()));
        pipeline.addLast(beanFactory.getBean(HttpRequestDispatcher.class));
    }
}
