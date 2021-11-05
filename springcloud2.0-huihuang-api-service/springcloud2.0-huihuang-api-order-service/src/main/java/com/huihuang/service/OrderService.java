package com.huihuang.service;

import com.huihuang.entiy.User;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

public interface OrderService {

    @RequestMapping("/getMemberList")
    public List<User> getMemberList(String name);

    @RequestMapping("/saveFile")
    public String saveFile();
}
