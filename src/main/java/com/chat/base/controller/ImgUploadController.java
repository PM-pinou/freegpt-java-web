package com.chat.base.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.UUID;

@RestController
public class ImgUploadController extends BaseController {
    /**
     * 时间格式化
     */
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/");

    @Value("${file-save-path}")
    private String fileSavePath;


    @PostMapping("/upload")
    public String uploadFile(MultipartFile file, HttpServletRequest request) {
        //后半段目录：  2020/03/15
        /**
         * 文件保存目录  E:/images/2020/03/15/
         * 如果目录不存在，则创建
         */
        File dir = new File(fileSavePath );
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //给文件重新设置一个名字
        //后缀
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String newFileName= UUID.randomUUID().toString().replaceAll("-", "")+suffix;

        //创建这个新文件
        File newFile = new File(fileSavePath  + newFileName);
        //复制操作
        try {
            file.transferTo(newFile);
            //协议 :// ip地址 ：端口号 / 文件目录(/images/2020/03/15/xxx.jpg)
            return fileSavePath+ newFileName;
        } catch (IOException e) {
            return "";
        }
    }
}
