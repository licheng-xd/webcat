package com.lchml.webcat.config;

import org.springframework.context.annotation.Configuration;

/**
 * Created by lc on 11/16/17.
 */
@Configuration
public class WebcatHttpConf {

    private boolean logEnable = true;

    private String defaultProduce = "application/json;charset=utf-8";

    private boolean logResponse = false;

    private int maxPayload = 1048576;

    public boolean isLogEnable() {
        return logEnable;
    }

    public void setLogEnable(boolean logEnable) {
        this.logEnable = logEnable;
    }

    public String getDefaultProduce() {
        return defaultProduce;
    }

    public void setDefaultProduce(String defaultProduce) {
        this.defaultProduce = defaultProduce;
    }

    public boolean isLogResponse() {
        return logResponse;
    }

    public void setLogResponse(boolean logResponse) {
        this.logResponse = logResponse;
    }

    public int getMaxPayload() {
        return maxPayload;
    }

    public void setMaxPayload(int maxPayload) {
        this.maxPayload = maxPayload;
    }
}
