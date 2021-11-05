package com.huihuang.service;

import com.huihuang.entiy.User;
import feign.feign.MemberServiceFeign;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@RestController
public class OrderServiceImpl implements OrderService{

    @Autowired
    private MemberServiceFeign memberService;

    @Override
    public List<User> getMemberList(String name) {
        return memberService.getUsers(name);
    }

    @Override
    public String saveFile() {
        File file = new File("C:\\Users\\Raiden\\Desktop\\upload.txt");
        DiskFileItem fileItem = (DiskFileItem) new DiskFileItemFactory().createItem("file",
                MediaType.TEXT_PLAIN_VALUE, true, file.getName());

        try (InputStream input = new FileInputStream(file); OutputStream os = fileItem.getOutputStream()) {
            IOUtils.copy(input, os);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid file: " + e, e);
        }

        MultipartFile multi = new CommonsMultipartFile(fileItem);

        return memberService.handleFileUpload(multi);
    }
}
