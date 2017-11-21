package com.lchml.webcat.webscoket;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 维护外部客户端与link之间的连接关系
 *
 * Created by lc on 11/11/17.
 */
public class ChannelInfoHub {
    private static Logger logger = LoggerFactory.getLogger(ChannelInfoHub.class);

    private static ChannelInfoHub _instance = null;
    private static ConcurrentMap<String, Channel> clientsMap = new ConcurrentHashMap<String, Channel>();

    private ChannelInfoHub() {
    }

    public static synchronized ChannelInfoHub getInstance() {
        if (_instance == null) {
            _instance = new ChannelInfoHub();
        }
        return _instance;
    }

    public int getCurrentClientCount(){
        return clientsMap.keySet().size();
    }

    public Channel getClient(String conid) {
        return clientsMap.get(conid);
    }

    public void putClient(Channel client) {
        ChannelInfo ci = client.attr(ChannelInfo.ATTRIBUTE_KEY).get();
        if (ci == null)
            return;
        clientsMap.putIfAbsent(client.id().toString(), client);
    }

    public void removeClient(String connid) {
        Channel client = clientsMap.remove(connid);
        if (client != null) {
            client.close();
        }
    }
}
