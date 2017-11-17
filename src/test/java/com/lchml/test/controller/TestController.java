package com.lchml.test.controller;

import com.lchml.webcat.annotation.HttpController;
import com.lchml.webcat.annotation.HttpRequestMapping;
import com.lchml.webcat.annotation.ReqBody;
import com.lchml.webcat.annotation.ReqMethod;
import com.lchml.webcat.util.ResponseUtil;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * Created by lc on 11/15/17.
 */
@HttpController(path = "/test")
public class TestController {

    @HttpRequestMapping(path = "/hello", method = {}, consumes = {"text/plain"})
    public String testHello() {
        return "hello webcat";
    }

    @HttpRequestMapping(path = "/bodytest", method = {ReqMethod.POST})
    public String testBody(@ReqBody String body) {
        return "hello webcat " + body;
    }

    @HttpRequestMapping(path = "/redirect", method = {ReqMethod.GET})
    public void testRedirect(FullHttpResponse response) {
        ResponseUtil.redirect(response, "https://www.baidu.com");
    }
}
