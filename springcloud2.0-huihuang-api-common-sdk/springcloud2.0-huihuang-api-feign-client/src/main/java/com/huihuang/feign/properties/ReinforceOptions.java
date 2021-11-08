package com.huihuang.feign.properties;

import feign.Request;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 18:44 2021/11/6
 * @Modified By:
 */
public class ReinforceOptions {

    private long connectTimeout;
    private TimeUnit connectTimeoutUnit;
    private long readTimeout;
    private TimeUnit readTimeoutUnit;
    private boolean followRedirects;

    private int maxAutoRetriesNextServer;
    private int maxAutoRetries;

    private Method method;

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public TimeUnit getConnectTimeoutUnit() {
        return connectTimeoutUnit;
    }

    public void setConnectTimeoutUnit(TimeUnit connectTimeoutUnit) {
        this.connectTimeoutUnit = connectTimeoutUnit;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public TimeUnit getReadTimeoutUnit() {
        return readTimeoutUnit;
    }

    public void setReadTimeoutUnit(TimeUnit readTimeoutUnit) {
        this.readTimeoutUnit = readTimeoutUnit;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public int getMaxAutoRetriesNextServer() {
        return maxAutoRetriesNextServer;
    }

    public void setMaxAutoRetriesNextServer(int maxAutoRetriesNextServer) {
        this.maxAutoRetriesNextServer = maxAutoRetriesNextServer;
    }

    public int getMaxAutoRetries() {
        return maxAutoRetries;
    }

    public void setMaxAutoRetries(int maxAutoRetries) {
        this.maxAutoRetries = maxAutoRetries;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public static class Options extends Request.Options{

        private int maxAutoRetriesNextServer;
        private int maxAutoRetries;
        private Method method;

        public Options(ReinforceOptions options){
            super(options.connectTimeout, options.connectTimeoutUnit, options.readTimeout, options.readTimeoutUnit, options.followRedirects);
            this.maxAutoRetries = options.maxAutoRetries;
            this.maxAutoRetriesNextServer = options.maxAutoRetriesNextServer;
            this.method = options.method;
        }

        public int getMaxAutoRetriesNextServer() {
            return maxAutoRetriesNextServer;
        }

        public void setMaxAutoRetriesNextServer(int maxAutoRetriesNextServer) {
            this.maxAutoRetriesNextServer = maxAutoRetriesNextServer;
        }

        public int getMaxAutoRetries() {
            return maxAutoRetries;
        }

        public void setMaxAutoRetries(int maxAutoRetries) {
            this.maxAutoRetries = maxAutoRetries;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }
    }

    public Request.Options options(){
        return new ReinforceOptions.Options(this);
    }
}
