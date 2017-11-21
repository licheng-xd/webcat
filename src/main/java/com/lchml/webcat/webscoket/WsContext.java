package com.lchml.webcat.webscoket;

import io.netty.channel.Channel;

import java.util.Map;

/**
 * Created by lc on 11/20/17.
 */
public class WsContext {
    private String path;

    private ChannelInfo ci;

    private int mid;

    private int version;

    private Channel channel;

    private Map<String, Object> params;

    public WsContext(String path, ChannelInfo ci, int mid, int version, Channel channel, Map<String, Object> params) {
        this.path = path;
        this.ci = ci;
        this.mid = mid;
        this.version = version;
        this.channel = channel;
        this.params = params;
    }

    public ChannelInfo getCi() {
        return ci;
    }

    public String getPath() {
        return path;
    }

    public int getMid() {
        return mid;
    }

    public int getVersion() {
        return version;
    }

    public Channel getChannel() {
        return channel;
    }

    public Map<String, Object> getParams() {
        return params;
    }

}
