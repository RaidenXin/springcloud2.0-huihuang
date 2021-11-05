//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package feign;

import com.alibaba.cloud.sentinel.feign.SentinelContractHolder;
import com.alibaba.cloud.sentinel.feign.SentinelTargeterAspect;
import feign.Contract.Default;
import feign.hystrix.FallbackFactory;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public final class CustomSentinelFeign {
    private CustomSentinelFeign() {
    }

    public static CustomSentinelFeign.Builder builder() {
        return new CustomSentinelFeign.Builder();
    }

    public static final class Builder extends feign.Feign.Builder implements ApplicationContextAware {
        private Contract contract = new Default();
        private ApplicationContext applicationContext;
        private FeignContext feignContext;

        public Builder() {
        }

        public feign.Feign.Builder invocationHandlerFactory(InvocationHandlerFactory invocationHandlerFactory) {
            throw new UnsupportedOperationException();
        }

        public CustomSentinelFeign.Builder contract(Contract contract) {
            this.contract = contract;
            return this;
        }

        public Feign build() {
            super.invocationHandlerFactory(new InvocationHandlerFactory() {
                public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
                    Object feignClientFactoryBean = SentinelTargeterAspect.getFeignClientFactoryBean();
                    if (feignClientFactoryBean != null) {
                        Class fallback = (Class)Builder.this.getFieldValue(feignClientFactoryBean, "fallback");
                        Class fallbackFactory = (Class)Builder.this.getFieldValue(feignClientFactoryBean, "fallbackFactory");
                        String beanName = (String)Builder.this.getFieldValue(feignClientFactoryBean, "contextId");
                        if (!StringUtils.hasText(beanName)) {
                            beanName = (String)Builder.this.getFieldValue(feignClientFactoryBean, "name");
                        }

                        if (Void.TYPE != fallback) {
                            Object fallbackInstance = this.getFromContext(beanName, "fallback", fallback, target.type());
                            return new CustomSentinelInvocationHandler(target, dispatch, new feign.hystrix.FallbackFactory.Default(fallbackInstance));
                        }

                        if (Void.TYPE != fallbackFactory) {
                            FallbackFactory fallbackFactoryInstance = (FallbackFactory)this.getFromContext(beanName, "fallbackFactory", fallbackFactory, FallbackFactory.class);
                            return new CustomSentinelInvocationHandler(target, dispatch, fallbackFactoryInstance);
                        }
                    }

                    return new CustomSentinelInvocationHandler(target, dispatch);
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

        private Object getFieldValue(Object instance, String fieldName) {
            Field field = ReflectionUtils.findField(instance.getClass(), fieldName);
            field.setAccessible(true);

            try {
                return field.get(instance);
            } catch (IllegalAccessException var5) {
                return null;
            }
        }

        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
            this.feignContext = (FeignContext)this.applicationContext.getBean(FeignContext.class);
        }
    }
}
