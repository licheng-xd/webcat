package com.lchml.test;

import com.lchml.webcat.ex.WebcatStartException;
import com.lchml.webcat.http.HttpServer;
import com.lchml.webcat.http.WebcatHttpServer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by lc on 11/15/17.
 */
public class WebcatTest {

    public static void main(String[] args) throws WebcatStartException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("webtest.xml");
        // console
        HttpServer httpServer = context.getBean(WebcatHttpServer.class);
        httpServer.setPort(8080);
        httpServer.start();
    }
}
