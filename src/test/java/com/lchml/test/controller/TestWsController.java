package com.lchml.test.controller;

import com.lchml.webcat.annotation.WsController;
import com.lchml.webcat.annotation.WsRequestMapping;

/**
 * Created by lc on 11/20/17.
 */
@WsController(path = "/test")
public class TestWsController {

    @WsRequestMapping(path = "/hello")
    public Object testHello(String name) {
        return "hello webcat " + name;
    }
}
