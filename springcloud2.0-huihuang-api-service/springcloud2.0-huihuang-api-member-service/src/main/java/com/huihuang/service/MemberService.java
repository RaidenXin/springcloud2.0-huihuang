package com.huihuang.service;

import com.huihuang.entiy.User;
import com.raiden.feign.annotation.RpcInfo;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 注解放在 interface 上,则该 interface 下所有的方法 共享该配置
 */
@RpcInfo(connectTimeout = 2,connectTimeoutUnit = TimeUnit.SECONDS, readTimeout = 1, readTimeoutUnit = TimeUnit.SECONDS, followRedirects = false)
public interface MemberService {

    /**
     * 注解放在 method 上
     * 则该 method  RPC调用 时使用该配置。
     * 方法上的配置优先级大于 interface 级别,
     * 同时存在时,以 method 上的为准
     * connectTimeout 连接超时时间
     * connectTimeoutUnit 连接超时时间的单位 默认为毫秒
     * readTimeout 读取超时时间
     * readTimeoutUnit 读取超时时间单位 默认为毫秒
     * followRedirects 是否重定向 默认为否
     * maxAutoRetriesNextServer 切换实例的重试次数 默认为 0 次 禁止重试
     * maxAutoRetries 对当前实例的重试次数 默认为 0 次 禁止重试
     * isAllowedRetry 是否允许重试 默认为 false 不允许
     */
    @RequestMapping("/getUsers")
    @RpcInfo(connectTimeout = 2,connectTimeoutUnit = TimeUnit.SECONDS,
            readTimeout = 1, readTimeoutUnit = TimeUnit.SECONDS, followRedirects = false,
            maxAutoRetriesNextServer = 1, maxAutoRetries = 1, isAllowedRetry = true)
    List<User> getUsers(String name);

    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RpcInfo(connectTimeout = 5,connectTimeoutUnit = TimeUnit.SECONDS, readTimeout = 2, readTimeoutUnit = TimeUnit.SECONDS)
    String handleFileUpload(@RequestPart(value = "file") MultipartFile file);
}
