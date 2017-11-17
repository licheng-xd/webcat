package com.lchml.webcat.http;

import com.lchml.webcat.ex.WebcatStartException;

/**
 * Created by lc on 11/16/17.
 */
public interface HttpServer {
    void setPort(int port);

    void start() throws WebcatStartException;
}
