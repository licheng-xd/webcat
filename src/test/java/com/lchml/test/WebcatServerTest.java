package com.lchml.test;

import com.lchml.webcat.ex.WebcatStartException;
import com.lchml.webcat.common.WebcatServer;
import com.lchml.webcat.http.WebcatHttpServer;
import com.lchml.webcat.webscoket.ChannelConnectListener;
import com.lchml.webcat.webscoket.ChannelDisconnectListener;
import com.lchml.webcat.webscoket.ChannelInfo;
import com.lchml.webcat.webscoket.WebcatWsServer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by lc on 11/15/17.
 */
public class WebcatServerTest {

    public static void main(String[] args) throws WebcatStartException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("webtest.xml");

        WebcatServer httpServer = context.getBean(WebcatHttpServer.class);
        httpServer.setPort(8080);
        httpServer.start();

        WebcatWsServer wsServer = context.getBean(WebcatWsServer.class);
        wsServer.setChannelConnectListener(new ChannelConnectListener() {
            @Override public void connect(ChannelInfo channelInfo) {
                System.out.println(channelInfo.getClientIp() + " connect");
            }
        });
        wsServer.setChannelDisconnectListener(new ChannelDisconnectListener() {
            @Override public void disconnect(ChannelInfo channelInfo) {
                System.out.println(channelInfo.getClientIp() + " disconnect");
            }
        });

        wsServer.setPort(8081);
        wsServer.start();
    }
}
