package com.huihuang.feign;

import com.huihuang.feign.annotation.RpcInfo;
import com.huihuang.feign.properties.ReinforceFeignProperties;
import com.huihuang.feign.properties.ReinforceOptions;
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

    private ReinforceFeignProperties properties;

    public AbstractReinforceInvocationHandler(ReinforceFeignProperties properties){
        this.properties = properties;
    }

    protected void initRpcInfo(Map<Method, InvocationHandlerFactory.MethodHandler> dispatch){
        final Map<String, ReinforceOptions> rpcConfig = properties.getRpcConfig();
        dispatch.entrySet().stream().forEach(e -> {
            Method method = e.getKey();
            InvocationHandlerFactory.MethodHandler methodHandler = e.getValue();
            Class<?> declaringClass = method.getDeclaringClass();
            StringBuilder configKey = new StringBuilder(declaringClass.getSimpleName());
            configKey.append(method.getName());
            for (Class<?> c : method.getParameterTypes()){
                configKey.append(c.getSimpleName());
            }
            ReinforceOptions reinforceOptions = rpcConfig.get(configKey.toString());
            Request.Options options = null;
            if (reinforceOptions == null){
                RpcInfo annotation = method.getAnnotation(RpcInfo.class);
                if (annotation == null){
                    //从父类头上获取
                    annotation = declaringClass.getAnnotation(RpcInfo.class);
                }
                if (annotation != null){
                    options = new Request.Options(annotation.connectTimeout(), annotation.connectTimeoutUnit(), annotation.readTimeout(), annotation.readTimeoutUnit(), annotation.followRedirects());
                }
            }else {
                options = reinforceOptions.options();
            }
            if (options != null){
                FieldUtils.setFieldValue(methodHandler, "options", options);
            }
        });
    }
}
