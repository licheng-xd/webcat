package com.lchml.webcat.util;

import com.lchml.webcat.annotation.ReqMethod;
import com.lchml.webcat.http.HttpRequestData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by lc on 11/16/17.
 */
public class RequestUtil {
    private static final Logger logger = LoggerFactory.getLogger(RequestUtil.class);

    private static final String ipDigitPattern = "(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)";

    //内网地址模式
    private static final Pattern internalIpPattern = Pattern.compile(
        String.format("(10(\\.%s){3})|(172\\.(1[6-9]|2\\d|3[01])(\\.%s){2})|(192\\.168(\\.%s){2})|(127\\.0\\.0\\.%s)",
            ipDigitPattern, ipDigitPattern, ipDigitPattern, ipDigitPattern));


    public static HttpRequestData parseRequest(FullHttpRequest request) throws
        IOException {
        HttpRequestData data = new HttpRequestData();
        Map<String, String> params = new HashMap<String, String>();
        QueryStringDecoder urlDecoder = new QueryStringDecoder(request.uri());
        for (Map.Entry<String, List<String>> entry : urlDecoder.parameters().entrySet()) {
            params.put(entry.getKey(), entry.getValue().get(0));
        }
        if (request.method() == HttpMethod.POST || request.method() == HttpMethod.PUT) {
            String body = request.content().toString(CharsetUtil.UTF_8);
            data.setBody(body);
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
            decoder.offer(request);
            List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();
            for (InterfaceHttpData parm : parmList) {
                Attribute attr = (Attribute) parm;
                params.put(attr.getName(), attr.getValue());
            }
        }
        data.setParams(params);
        return data;
    }

    public static boolean supportMethod(ReqMethod[] methods, String target) {
        if (methods.length == 0) {
            return true;
        }
        for (ReqMethod m : methods) {
            if (target.toUpperCase().contains(m.name().toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    public static boolean supportContentType(String[] consumers, String target) {
        if (consumers.length == 0) {
            return true;
        }
        for (String c : consumers) {
            if (target != null && target.toLowerCase().contains(c.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static String checkHeaders(String[] headers, HttpHeaders requesHeads) {
        if (headers.length == 0) {
            return null;
        }
        for (String h : headers) {
            if (requesHeads.get(h) == null) {
                return h;
            }
        }
        return null;
    }

    public static String getRealIp(HttpRequest request, ChannelHandlerContext ctx) {
        try {
            String realIp = null;
            // 优先拿前端nginx放在forward包头里的ip
            String xff = request.headers().get("X-Forwarded-For");
            if (!StringUtils.isEmpty(xff)) {
                String[] ips = xff.split(","); //取出X-Forwarded-For中的第一个外网ip
                if (ips.length > 0) {
                    for (String ip1 : ips) {
                        // 2g/3g网关可能在该包头里放一个内网ip，需要过滤
                        String ip = ip1.trim();
                        if (!isInternalIp(ip)) { //过滤掉前面的内网ip
                            realIp = ip;
                            break;
                        }
                    }
                    if (StringUtils.isEmpty(realIp)) {
                        //所有ip都是内网ip,则取第一个ip作为真实ip
                        realIp = ips[0].trim();
                    }
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("ips:" + JsonUtil.toJson(ips) + " first pub ip:" + realIp );
                }
            }
            if (StringUtils.isEmpty(realIp)) {  //X-Forwarded-For中取不到任何ip,则用直连的代理ip作为真实ip
                logger.debug("X-Forwarded-For contains no external ip: " + xff);
                InetSocketAddress addr = (InetSocketAddress) ctx.channel().remoteAddress();
                realIp = addr.getAddress().getHostAddress();
            }
            realIp = realIp.trim();
            return realIp;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static boolean isInternalIp(String str) {
        return internalIpPattern.matcher(str).matches();
    }
}
