package com.huihuang.service;

import com.huihuang.entiy.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class MemberServiceImpl implements MemberService{

    @Override
    public List<User> getUsers(String name) {
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setId("1");
        user1.setAge(12);
        user1.setName("zhangsan");
        User user2 = new User();
        user2.setId("2");
        user2.setAge(20);
        user2.setName("lisi");
        users.add(user1);
        users.add(user2);
        return users;
    }

    @Override
    public String handleFileUpload(MultipartFile file) {
        try {
            StringBuilder builder = new StringBuilder();
            byte[] bytes = new byte[1024];
            InputStream inputStream = file.getInputStream();
            while (inputStream.read(bytes) != -1){
                String str = new String(bytes, "GBK");
                builder.append(str);
            }
            log.info(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getName();
    }
}
