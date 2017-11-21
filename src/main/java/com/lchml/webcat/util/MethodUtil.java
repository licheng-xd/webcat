package com.lchml.webcat.util;

import com.lchml.webcat.annotation.ReqBody;
import com.lchml.webcat.annotation.ReqParam;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

public class MethodUtil {
    public static Map<Integer, String> getParameterMap(Method method, Map<Integer, String> paramIndexMap,
        Map<Integer, ReqParam> paramAnnoMap, Map<Integer, ReqBody> bodyAnno) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        Class<?> clazz = method.getDeclaringClass();
        CtClass clz = pool.getCtClass(clazz.getName());
        CtClass[] params = new CtClass[method.getParameterTypes().length];
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            params[i] = pool.getCtClass(method.getParameterTypes()[i].getName());
        }
        CtMethod cm = clz.getDeclaredMethod(method.getName(), params);
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        int pos = 0;
        while (!Modifier.isStatic(cm.getModifiers()) && !"this".equals(attr.variableName(pos++))) {
        }

        Annotation[][] annos = method.getParameterAnnotations();
        for (int i = 0; i < cm.getParameterTypes().length; i++) {
            paramIndexMap.put(i, attr.variableName(i + pos));
            Annotation[] annosParam = annos[i];
            for (Annotation anno : annosParam) {
                if (ReqParam.class.isInstance(anno)) {
                    ReqParam req = (ReqParam) anno;
                    paramAnnoMap.put(i, req);
                    if (!StringUtils.isEmpty(req.name())) {
                        paramIndexMap.put(i, req.name());
                    }
                } else if (ReqBody.class.isInstance(anno)) {
                    if (bodyAnno != null) {
                        bodyAnno.put(i, (ReqBody) anno);
                    }
                }
            }
        }
        return paramIndexMap;
    }
}
