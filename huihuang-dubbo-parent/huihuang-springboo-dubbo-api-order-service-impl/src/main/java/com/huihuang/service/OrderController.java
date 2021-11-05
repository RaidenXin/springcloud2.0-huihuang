package com.huihuang.service;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Reference
    private MemberService memberService;

    @GetMapping("/orderToMember")
    public String orderToMember(){
        return memberService.getUser();
    }
}
