package com.lchml.test.controller;

import com.lchml.webcat.annotation.WsController;
import com.lchml.webcat.annotation.WsRequestMapping;
import com.lchml.webcat.webscoket.WsContext;

/**
 * Created by lc on 11/20/17.
 */
@WsController(path = "/test")
public class TestWsController {

    @WsRequestMapping(path = "/hello")
    public Object testHello(String name, WsContext ctx) {
        return "hello webcat " + name + " from " + ctx.getCi().getClientIp();
    }
}
