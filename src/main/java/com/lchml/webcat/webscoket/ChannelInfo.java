package com.lchml.webcat.webscoket;

import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.util.Map;

/**
 * Created by lc on 11/11/17.
 */
public class ChannelInfo {
    public static final AttributeKey<ChannelInfo> ATTRIBUTE_KEY = AttributeKey.valueOf("CI");

    private String clientIp;

    private Map<String, Object> attr;

    public ChannelInfo() {
        attr = Maps.newHashMap();
    }

    public void init(Channel channel) throws Exception {
        if (channel != null) {
            //将ChannelInfo绑定到Channel中
            channel.attr(ChannelInfo.ATTRIBUTE_KEY).set(this);
            //将channel放到outterclienthub中
            ChannelInfoHub.getInstance().putClient(channel);
        } else {
            throw new Exception("Channel is null!");
        }
    }

    public static void sendRawToClient(ChannelHandlerContext ctx, Object buff){
        sendRawToClient(ctx, buff, null);
    }

    public static void sendRawToClient(ChannelHandlerContext ctx, Object buff, ChannelFutureListener writeDoneFutrue) {
        ChannelFuture cf = ctx.channel().writeAndFlush(buff);
        if (writeDoneFutrue != null) {
            cf.addListener(writeDoneFutrue);
        }
    }

    public static ChannelInfo getChannelInfo(Channel channel) {
        return (channel == null) ? null
                : channel.attr(ChannelInfo.ATTRIBUTE_KEY).get();
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public Map<String, Object> getAttr() {
        return attr;
    }

    public void setAttr(Map<String, Object> attr) {
        this.attr = attr;
    }

    public void addAttr(String key, Object val) {
        this.attr.put(key, val);
    }

    public Object removeAttr(String key) {
        return this.attr.remove(key);
    }
}
