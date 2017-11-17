package com.lchml.webcat.http;

import java.util.Map;

/**
 * Created by lc on 11/16/17.
 */
public class HttpRequestData {

    private Map<String, String> params;

    private String body;

    private String ip;

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(
        Map<String, String> params) {
        this.params = params;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
