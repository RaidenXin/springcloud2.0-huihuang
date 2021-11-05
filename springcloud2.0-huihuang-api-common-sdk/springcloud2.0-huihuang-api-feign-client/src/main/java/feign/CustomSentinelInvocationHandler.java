package feign;

import java.lang.reflect.*;
import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.cloud.sentinel.feign.SentinelContractHolder;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import feign.annotation.RpcInfo;
import feign.InvocationHandlerFactory.MethodHandler;
import feign.hystrix.FallbackFactory;
import org.springframework.util.ReflectionUtils;

import static feign.Util.checkNotNull;

/**
 * {@link InvocationHandler} handle invocation that protected by Sentinel.
 *
 * @author <a href="mailto:fangjian0423@gmail.com">Jim</a>
 */
public class CustomSentinelInvocationHandler implements InvocationHandler {

	private final Target<?> target;

	private final Map<Method, MethodHandler> dispatch;

	private FallbackFactory fallbackFactory;

	private Map<Method, Method> fallbackMethodMap;

	CustomSentinelInvocationHandler(Target<?> target, Map<Method, MethodHandler> dispatch,
                                    FallbackFactory fallbackFactory) {
		this.target = checkNotNull(target, "target");
		this.dispatch = checkNotNull(dispatch, "dispatch");
		this.fallbackFactory = fallbackFactory;
		this.fallbackMethodMap = toFallbackMethod(dispatch);
		init();
	}

	CustomSentinelInvocationHandler(Target<?> target, Map<Method, MethodHandler> dispatch) {
		this.target = checkNotNull(target, "target");
		this.dispatch = checkNotNull(dispatch, "dispatch");
		init();
	}

	private void init(){
        dispatch.entrySet().stream().forEach(e -> {
            Method method = e.getKey();
            MethodHandler methodHandler = e.getValue();
            if (methodHandler instanceof SynchronousMethodHandler){
                SynchronousMethodHandler synchronousMethodHandler = (SynchronousMethodHandler) methodHandler;
                RpcInfo annotation = method.getAnnotation(RpcInfo.class);
                Request.Options options;
                if (annotation == null){
                    //从父类头上获取
                    annotation = method.getDeclaringClass().getAnnotation(RpcInfo.class);
                }
                if (annotation != null){
                    options = new Request.Options(annotation.connectTimeout(), annotation.connectTimeoutUnit(), annotation.readTimeout(), annotation.readTimeoutUnit(), annotation.followRedirects());
                    setFieldValue(synchronousMethodHandler, "options", options);
                }
            }
        });
    }

    private void setFieldValue(Object instance, String fieldName,Object value) {
        Field field = ReflectionUtils.findField(instance.getClass(), fieldName);
        field.setAccessible(true);
        try {
            field.set(instance, value);
        } catch (IllegalAccessException var5) {
        }
    }

	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args)
			throws Throwable {
		if ("equals".equals(method.getName())) {
			try {
				Object otherHandler = args.length > 0 && args[0] != null
						? Proxy.getInvocationHandler(args[0]) : null;
				return equals(otherHandler);
			}
			catch (IllegalArgumentException e) {
				return false;
			}
		}
		else if ("hashCode".equals(method.getName())) {
			return hashCode();
		}
		else if ("toString".equals(method.getName())) {
			return toString();
		}

		Object result;
		MethodHandler methodHandler = this.dispatch.get(method);
		// only handle by HardCodedTarget
		if (target instanceof Target.HardCodedTarget) {
			Target.HardCodedTarget hardCodedTarget = (Target.HardCodedTarget) target;
			MethodMetadata methodMetadata = SentinelContractHolder.METADATA_MAP
					.get(hardCodedTarget.type().getName()
							+ Feign.configKey(hardCodedTarget.type(), method));
			// resource default is HttpMethod:protocol://url
			if (methodMetadata == null) {
				result = methodHandler.invoke(args);
			}
			else {
				String resourceName = methodMetadata.template().method().toUpperCase()
						+ ":" + hardCodedTarget.url() + methodMetadata.template().path();
				Entry entry = null;
				try {
					ContextUtil.enter(resourceName);
					entry = SphU.entry(resourceName, EntryType.OUT, 1, args);
					result = methodHandler.invoke(args);
				}
				catch (Throwable ex) {
					// fallback handle
					if (!BlockException.isBlockException(ex)) {
						Tracer.traceEntry(ex, entry);
					}
					if (fallbackFactory != null) {
						try {
							Object fallbackResult = fallbackMethodMap.get(method)
									.invoke(fallbackFactory.create(ex), args);
							return fallbackResult;
						}
						catch (IllegalAccessException e) {
							// shouldn't happen as method is public due to being an
							// interface
							throw new AssertionError(e);
						}
						catch (InvocationTargetException e) {
							throw new AssertionError(e.getCause());
						}
					}
					else {
						// throw exception if fallbackFactory is null
						throw ex;
					}
				}
				finally {
					if (entry != null) {
						entry.exit(1, args);
					}
					ContextUtil.exit();
				}
			}
		}
		else {
			// other target type using default strategy
			result = methodHandler.invoke(args);
		}

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CustomSentinelInvocationHandler) {
			CustomSentinelInvocationHandler other = (CustomSentinelInvocationHandler) obj;
			return target.equals(other.target);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return target.hashCode();
	}

	@Override
	public String toString() {
		return target.toString();
	}

	static Map<Method, Method> toFallbackMethod(Map<Method, MethodHandler> dispatch) {
		Map<Method, Method> result = new LinkedHashMap<>();
		for (Method method : dispatch.keySet()) {
			method.setAccessible(true);
			result.put(method, method);
		}
		return result;
	}

}
