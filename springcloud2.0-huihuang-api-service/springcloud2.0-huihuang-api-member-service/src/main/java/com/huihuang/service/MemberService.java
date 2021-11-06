package com.huihuang.service;

import com.huihuang.entiy.User;
import com.huihuang.feign.annotation.RpcInfo;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface MemberService {

    @RequestMapping("/getUsers")
    @RpcInfo(connectTimeout = 2,connectTimeoutUnit = TimeUnit.SECONDS, readTimeout = 1, readTimeoutUnit = TimeUnit.SECONDS, followRedirects = false)
    public List<User> getUsers(String name);

    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RpcInfo(connectTimeout = 5,connectTimeoutUnit = TimeUnit.SECONDS, readTimeout = 2, readTimeoutUnit = TimeUnit.SECONDS, followRedirects = false)
    public String handleFileUpload(@RequestPart(value = "file") MultipartFile file);
}
