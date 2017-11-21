package com.lchml.webcat.common;

import com.lchml.webcat.ex.WebcatStartException;

/**
 * Created by lc on 11/16/17.
 */
public interface WebcatServer {
    void setPort(int port);

    void start() throws WebcatStartException;
}
