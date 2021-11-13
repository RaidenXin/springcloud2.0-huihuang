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
        ReinforceOptions defaultOptions = rpcConfig.get("default");
        dispatch.entrySet().stream().forEach(e -> {
            Method method = e.getKey();
            InvocationHandlerFactory.MethodHandler methodHandler = e.getValue();
            ReinforceOptions reinforceOptions = getReinforceOptions(rpcConfig, method, defaultOptions);
            ReinforceOptions.Options options = null;
            if (reinforceOptions == null){
                RpcInfo annotation = method.getAnnotation(RpcInfo.class);
                //如果从方法上没找到
                if (annotation == null){
                    //从父类头上获取
                    annotation = method.getDeclaringClass().getAnnotation(RpcInfo.class);
                }
                if (annotation != null){
                    options = new ReinforceOptions.Options(annotation);
                }
            }else {
                //是否重试
                boolean retry = reinforceOptions.isRetry();
                options = reinforceOptions.options();
                if (retry){
                    RpcInfo annotation = method.getAnnotation(RpcInfo.class);
                    //如果从方法上没找到
                    if (annotation == null){
                        //从父类头上获取
                        annotation = method.getDeclaringClass().getAnnotation(RpcInfo.class);
                    }
                    boolean allowedRetry = annotation == null ? false : annotation.isAllowedRetry();
                    options.setAllowedRetry(allowedRetry);
                }
            }
            if (options != null){
                FieldUtils.setFieldValue(methodHandler, "options", options);
            }
        });
    }

    private ReinforceOptions getReinforceOptions(Map<String, ReinforceOptions> rpcConfig,Method method,ReinforceOptions defaultOptions){
        Class<?> declaringClass = method.getDeclaringClass();
        String simpleName = declaringClass.getSimpleName();
        StringBuilder configKey = new StringBuilder(simpleName);
        String methodName = method.getName();
        configKey.append(methodName);
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> c : parameterTypes){
            configKey.append(c.getSimpleName());
        }
        ReinforceOptions reinforceOptions = rpcConfig.get(configKey.toString());
        if (reinforceOptions != null){
            return reinforceOptions;
        }
        String name = declaringClass.getName();
        configKey = new StringBuilder(name);
        configKey.append(methodName);
        for (Class<?> c : parameterTypes){
            configKey.append(c.getSimpleName());
        }
        reinforceOptions = rpcConfig.get(configKey.toString());
        if (reinforceOptions != null){
            return reinforceOptions;
        }
        reinforceOptions = rpcConfig.get(simpleName);
        if (reinforceOptions != null){
            return reinforceOptions;
        }
        reinforceOptions = rpcConfig.get(name);
        if (reinforceOptions != null){
            return reinforceOptions;
        }
        return defaultOptions;
    }
}
