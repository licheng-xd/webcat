package com.lchml.webcat.config;

import org.springframework.context.annotation.Configuration;

/**
 * Created by lc on 11/16/17.
 */
@Configuration
public class WebcatConf {

    private boolean logEnable = true;

    private String defaultProduce = "application/json;charset=utf-8";

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
}
