//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.huihuang.feign;

import com.alibaba.cloud.sentinel.feign.SentinelContractHolder;
import com.alibaba.cloud.sentinel.feign.SentinelTargeterAspect;
import com.huihuang.feign.utils.FieldUtils;
import feign.Contract;
import feign.Contract.Default;
import feign.Feign;
import feign.InvocationHandlerFactory;
import feign.Target;
import feign.hystrix.FallbackFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;


public final class ReinforceSentinelFeign {
    private ReinforceSentinelFeign() {
    }

    public static ReinforceSentinelFeign.Builder builder() {
        return new ReinforceSentinelFeign.Builder();
    }

    public static final class Builder extends Feign.Builder implements ApplicationContextAware {
        private Contract contract = new Default();
        private ApplicationContext applicationContext;
        private FeignContext feignContext;

        public Builder() {
        }

        public feign.Feign.Builder invocationHandlerFactory(InvocationHandlerFactory invocationHandlerFactory) {
            throw new UnsupportedOperationException();
        }

        public ReinforceSentinelFeign.Builder contract(Contract contract) {
            this.contract = contract;
            return this;
        }

        public Feign build() {
            super.invocationHandlerFactory(new InvocationHandlerFactory() {
                public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
                    Object feignClientFactoryBean = SentinelTargeterAspect.getFeignClientFactoryBean();
                    if (feignClientFactoryBean != null) {
                        Class fallback = (Class) FieldUtils.getFieldValue(feignClientFactoryBean, "fallback");
                        Class fallbackFactory = (Class) FieldUtils.getFieldValue(feignClientFactoryBean, "fallbackFactory");
                        String beanName = (String) FieldUtils.getFieldValue(feignClientFactoryBean, "contextId");
                        if (!StringUtils.hasText(beanName)) {
                            beanName = (String) FieldUtils.getFieldValue(feignClientFactoryBean, "name");
                        }

                        if (Void.TYPE != fallback) {
                            Object fallbackInstance = this.getFromContext(beanName, "fallback", fallback, target.type());
                            return new ReinforceSentinelInvocationHandler(target, dispatch, new feign.hystrix.FallbackFactory.Default(fallbackInstance));
                        }

                        if (Void.TYPE != fallbackFactory) {
                            FallbackFactory fallbackFactoryInstance = (FallbackFactory)this.getFromContext(beanName, "fallbackFactory", fallbackFactory, FallbackFactory.class);
                            return new ReinforceSentinelInvocationHandler(target, dispatch, fallbackFactoryInstance);
                        }
                    }

                    return new ReinforceSentinelInvocationHandler(target, dispatch);
                }

                private Object getFromContext(String name, String type, Class fallbackType, Class targetType) {
                    Object fallbackInstance = Builder.this.feignContext.getInstance(name, fallbackType);
                    if (fallbackInstance == null) {
                        throw new IllegalStateException(String.format("No %s instance of type %s found for feign client %s", type, fallbackType, name));
                    } else if (!targetType.isAssignableFrom(fallbackType)) {
                        throw new IllegalStateException(String.format("Incompatible %s instance. Fallback/fallbackFactory of type %s is not assignable to %s for feign client %s", type, fallbackType, targetType, name));
                    } else {
                        return fallbackInstance;
                    }
                }
            });
            super.contract(new SentinelContractHolder(this.contract));
            return super.build();
        }

        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
            this.feignContext = (FeignContext)this.applicationContext.getBean(FeignContext.class);
        }
    }
}
