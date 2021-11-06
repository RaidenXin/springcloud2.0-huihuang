package com.huihuang.feign;

import com.huihuang.feign.annotation.RpcInfo;
import com.huihuang.feign.utils.FieldUtils;
import feign.InvocationHandlerFactory;
import feign.Request;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 17:18 2021/11/6
 * @Modified By:
 */
public abstract class AbstractReinforceInvocationHandler {

    public void initRpcInfo(Map<Method, InvocationHandlerFactory.MethodHandler> dispatch){
        dispatch.entrySet().stream().forEach(e -> {
            Method method = e.getKey();
            InvocationHandlerFactory.MethodHandler methodHandler = e.getValue();
            RpcInfo annotation = method.getAnnotation(RpcInfo.class);
            Request.Options options = null;
            if (annotation == null){
                //从父类头上获取
                annotation = method.getDeclaringClass().getAnnotation(RpcInfo.class);
            }
            if (annotation != null){
                options = new Request.Options(annotation.connectTimeout(), annotation.connectTimeoutUnit(), annotation.readTimeout(), annotation.readTimeoutUnit(), annotation.followRedirects());
            }
            if (options != null){
                FieldUtils.setFieldValue(methodHandler, "options", options);
            }
        });
    }
}
