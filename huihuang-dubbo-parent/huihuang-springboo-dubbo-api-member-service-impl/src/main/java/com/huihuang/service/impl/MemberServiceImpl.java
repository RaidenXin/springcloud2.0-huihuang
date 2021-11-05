package com.huihuang.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.huihuang.service.MemberService;
import org.springframework.beans.factory.annotation.Value;

@Service
public class MemberServiceImpl implements MemberService {

    @Value("${server.port}")
    private String port;
    @Override
    public String getUser() {
        return "订单服务调用会员服务~~~~\tport:" + port;
    }
}
