package com.lchml.webcat.webscoket;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lchml.webcat.annotation.ReqParam;
import com.lchml.webcat.annotation.WsController;
import com.lchml.webcat.annotation.WsRequestMapping;
import com.lchml.webcat.config.WebcatWsConf;
import com.lchml.webcat.ex.WebcatException;
import com.lchml.webcat.ex.WebcatInitException;
import com.lchml.webcat.util.*;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

/**
 * Created by lc on 11/20/17.
 */
@Component
public class WsRequestInvoker implements IWsReqeustInvoker {

    private final static Logger logger = LoggerFactory.getLogger(WsRequestInvoker.class);

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    @Autowired
    private WebcatWsConf webcatWsConf;

    private Map<String, Object> controllerMap; // controllerName - bean
    private Map<String, Method> methodMap; // path - method
    private Map<String, WsRequestMapping> requesAnnotMap; // path - requestAnno
    private Map<String, Map<Integer, String>> methodParamIndexMap;
    private Map<String, Map<Integer, ReqParam>> methodParamAnnoMap;
    private Map<String, String> pathMapping; // path - controllerName

    @PostConstruct
    public void init() {
        methodMap = Maps.newHashMap();
        pathMapping = Maps.newHashMap();
        methodParamIndexMap = Maps.newHashMap();
        methodParamAnnoMap = Maps.newHashMap();
        controllerMap = Maps.newHashMap();
        requesAnnotMap = Maps.newHashMap();
        Map<String, Object> controllers = beanFactory.getBeansWithAnnotation(WsController.class);
        for (Map.Entry<String, Object> entry : controllers.entrySet()) {
            controllerMap.put(entry.getKey(), entry.getValue());
            initPathMapping(entry.getValue(), entry.getKey());
        }
    }

    private void initPathMapping(Object controller, String controllerName) {
        Class<?> clazz = controller.getClass();
        WsController controllerAnno = controller.getClass().getAnnotation(WsController.class);
        while (clazz != Object.class) {
            Method[] methods = clazz.getDeclaredMethods();
            for (final Method method : methods) {
                if (method.getModifiers() == Modifier.PUBLIC && method.isAnnotationPresent(WsRequestMapping.class)) {
                    WsRequestMapping methodAnno = method.getAnnotation(WsRequestMapping.class);
                    if (Strings.isNullOrEmpty(methodAnno.path())) {
                        throw new WebcatInitException("HttpRequestMapping path can't be empty");
                    }
                    String fullPath = UrlUtil.resolveUrlPath(controllerAnno.path()) + UrlUtil.resolveUrlPath(methodAnno.path());
                    if (methodMap.containsKey(fullPath)) {
                        throw new WebcatInitException("HttpRequestMapping path must be unique");
                    }
                    requesAnnotMap.put(fullPath, methodAnno);
                    methodMap.put(fullPath, method);
                    try {
                        Map<Integer, String> paramIndex = Maps.newHashMap();
                        Map<Integer, ReqParam> paramAnno = Maps.newHashMap();
                        MethodUtil.getParameterMap(method, paramIndex, paramAnno, null);
                        methodParamIndexMap.put(fullPath, paramIndex);
                        methodParamAnnoMap.put(fullPath, paramAnno);
                    } catch (NotFoundException e) {
                        logger.error(e.getMessage(), e);
                    }
                    pathMapping.put(fullPath, controllerName);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    public boolean validPath(String path) {
        return methodMap.containsKey(path);
    }

    @Override
    public void invoke(WsContext ctx, WsResponse response) throws WebcatException {
        String path = ctx.getPath();
        Method method = methodMap.get(path);
        WebcatLog.setMethod(method.getName());
        String controllerName = pathMapping.get(path);
        Object controller = controllerMap.get(controllerName);
        try {
            WsRequestMapping mapping = requesAnnotMap.get(path);
            assert mapping != null;
            if (!checkVersion(mapping.versions(), ctx.getVersion())) {
                response.code(WsCode.VERSION_NOT_SUPPORT);
                return;
            }

            List<Object> convertedArgs = Lists.newArrayList();
            if (!assembleParam(convertedArgs, ctx, method.getParameterTypes(), methodParamIndexMap.get(path),
                methodParamAnnoMap.get(path), response)) {
                return;
            }
            // async response
            Object result = method.invoke(controller, convertedArgs.toArray(new Object[convertedArgs.size()]));
            // 设置response content
            response.code(WsCode.OK);
            if (result != null && !void.class.isAssignableFrom(result.getClass()) && !Void.class.isAssignableFrom(result.getClass())) {
                if (webcatWsConf.isLogResponse()) {
                    WebcatLog.setResponse(result);
                }
                response.payload(result);
            }

        } catch (Exception e) {
            throw new WebcatException("invoke error " + e.getMessage(), e);
        }
    }

    private static boolean checkVersion(int[] versions, int target) {
        if (versions.length == 0) {
            return true;
        }
        for (int v : versions) {
            if (target == v) {
                return true;
            }
        }
        return false;
    }

    private boolean assembleParam(List<Object> convertedArgs, WsContext ctx, Class<?>[] parameterTypes, Map<Integer, String> paramsIndex,
        Map<Integer, ReqParam> paramsAnno, WsResponse response) {
        assert paramsIndex != null && paramsAnno != null;
        Map<String, Object> args = ctx.getParams();
        for (Integer i = 0; i < parameterTypes.length; i++) {
            if (Long.class.isAssignableFrom(parameterTypes[i]) || long.class.isAssignableFrom(parameterTypes[i])) {
                Long arg = MapUtil.getLong(args, paramsIndex.get(i));
                if (arg == null) {
                    ReqParam anno = paramsAnno.get(i);
                    if (anno != null && !anno.required()) {
                        convertedArgs.add(Long.parseLong(anno.defaultValue()));
                    } else {
                        response.code(WsCode.MISS_PARAM).msg("miss param" + paramsIndex.get(i));
                        return false;
                    }
                } else {
                    convertedArgs.add(arg);
                }
            } else if (Integer.class.isAssignableFrom(parameterTypes[i]) || int.class.isAssignableFrom(parameterTypes[i])) {
                Integer arg = MapUtil.getInteger(args, paramsIndex.get(i));
                if (arg == null) {
                    ReqParam anno = paramsAnno.get(i);
                    if (anno != null && !anno.required()) {
                        convertedArgs.add(Integer.parseInt(anno.defaultValue()));
                    } else {
                        response.code(WsCode.MISS_PARAM).msg("miss param" + paramsIndex.get(i));
                        return false;
                    }
                } else {
                    convertedArgs.add(arg);
                }
            } else if (Double.class.isAssignableFrom(parameterTypes[i]) || double.class.isAssignableFrom(parameterTypes[i])) {
                Double arg = MapUtil.getDouble(args, paramsIndex.get(i));
                if (arg == null) {
                    ReqParam anno = paramsAnno.get(i);
                    if (anno != null && !anno.required()) {
                        convertedArgs.add(Double.parseDouble(anno.defaultValue()));
                    } else {
                        response.code(WsCode.MISS_PARAM).msg("miss param" + paramsIndex.get(i));
                        return false;
                    }
                } else {
                    convertedArgs.add(arg);
                }
            } else if (Float.class.isAssignableFrom(parameterTypes[i]) || float.class.isAssignableFrom(parameterTypes[i])) {
                Float arg = MapUtil.getFloat(args, paramsIndex.get(i));
                if (arg == null) {
                    ReqParam anno = paramsAnno.get(i);
                    if (anno != null && !anno.required()) {
                        convertedArgs.add(Float.parseFloat(anno.defaultValue()));
                    } else {
                        response.code(WsCode.MISS_PARAM).msg("miss param" + paramsIndex.get(i));
                        return false;
                    }
                } else {
                    convertedArgs.add(arg);
                }
            } else if (Boolean.class.isAssignableFrom(parameterTypes[i]) || boolean.class.isAssignableFrom(parameterTypes[i])) {
                Boolean arg = MapUtil.getBoolean(args, paramsIndex.get(i));
                if (arg == null) {
                    ReqParam anno = paramsAnno.get(i);
                    if (anno != null && !anno.required()) {
                        convertedArgs.add(Boolean.parseBoolean(anno.defaultValue()));
                    } else {
                        response.code(WsCode.MISS_PARAM).msg("miss param" + paramsIndex.get(i));
                        return false;
                    }
                } else {
                    convertedArgs.add(arg);
                }
            } else if (String.class.isAssignableFrom(parameterTypes[i])) {
                String arg = MapUtil.getString(args, paramsIndex.get(i));
                if (arg == null) {
                    ReqParam anno = paramsAnno.get(i);
                    if (anno != null && !anno.required()) {
                        convertedArgs.add(anno.defaultValue());
                    } else {
                        response.code(WsCode.MISS_PARAM).msg("miss param" + paramsIndex.get(i));
                    }
                } else {
                    convertedArgs.add(arg);
                }
            } else if (WsContext.class.isAssignableFrom(parameterTypes[i])) {
                convertedArgs.add(ctx);
            } else if (WsResponse.class.isAssignableFrom(parameterTypes[i])) {
                convertedArgs.add(response);
            }
        }
        return true;
    }
}
