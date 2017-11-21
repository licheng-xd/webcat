package com.lchml.webcat.webscoket;

import com.lchml.webcat.ex.WebcatException;

/**
 * Created by lc on 11/20/17.
 */
public interface IWsReqeustInvoker {

    void invoke(WsContext ctx, WsResponse response) throws WebcatException;
}
