package com.lchml.webcat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lc on 16/1/25.
 */
public class WebcatLog {
    private static final Logger logger = LoggerFactory.getLogger(WebcatLog.class);

    private static ThreadLocal<LogBean> logBean = new ThreadLocal<LogBean>();

    public static void init() {
        get().setProps(new HashMap<String, Object>());
    }

    public static void setRetcode(int code) {
        get().setRetcode(code);
    }

    public static void setStarttime(long starttime) {
        get().setStarttime(starttime);
    }

    public static void setSpendtime(long spendtime) {
        get().setSpendtime(spendtime);
    }

    public static void addProp(String key, Object value) {
        get().addProp(key, value);
    }

    public static void setProp(Map<String, Object> prop) {
        get().setProps(prop);
    }

    public static void setResponse(Object response) {
        String resp = null;
        if (response == null) {
            return;
        } else if (String.class.isAssignableFrom(response.getClass())) {
            resp = response.toString();
        } else {
            resp = JsonUtil.toJson(response);
        }
        get().setResponse(resp);
    }

    public static void setPath(String path) {
        get().setPath(path);
    }

    public static void setIp(String ip) {
        get().setIp(ip);
    }

    public static void setMethod(String method) {
        get().setMethod(method);
    }

    private static LogBean get() {
        LogBean lb = logBean.get();
        if (lb == null) {
            lb = LogBean.getLog();
            logBean.set(lb);
        }
        return lb;
    }

    public static void info() {
        logger.info(get().toString());
    }

    public static class LogBean {

        private int retcode;

        private String path;

        private String ip;

        private String method;

        private long starttime;

        private long spendtime;

        private String response;

        private Map<String, Object> props = new HashMap<String, Object>();

        private LogBean() {
            this.starttime = System.currentTimeMillis();
        }

        public static LogBean getLog() {
            return new LogBean();
        }

        public void addProp(String key, Object value) {
            props.put(key, value);
        }

        public void setProps(Map<String, Object> props) {
            this.props = props;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public void setStarttime(long starttime) {
            this.starttime = starttime;
        }

        public void setRetcode(int retcode) {
            this.retcode = retcode;
        }

        public void setSpendtime(long spendtime) {
            this.spendtime = spendtime;
        }

        public void setResponse(String response) {
            this.response = response;
        }

        @Override
        public String toString() {
            return JsonUtil.toJson(this);
        }

    }
}
