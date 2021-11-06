package com.huihuang.feign.properties;

import feign.Request;

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

    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setConnectTimeoutUnit(TimeUnit connectTimeoutUnit) {
        this.connectTimeoutUnit = connectTimeoutUnit;
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setReadTimeoutUnit(TimeUnit readTimeoutUnit) {
        this.readTimeoutUnit = readTimeoutUnit;
    }

    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public Request.Options options(){
        return new Request.Options(connectTimeout, connectTimeoutUnit, readTimeout, readTimeoutUnit, followRedirects);
    }
}
