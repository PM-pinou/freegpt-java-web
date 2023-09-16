package com.chat.base.encrypt;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class MySqlEncrypt {

    public static void main(String[] args) {

        StandardPBEStringEncryptor standardPBEStringEncryptor =new StandardPBEStringEncryptor();
        standardPBEStringEncryptor.setAlgorithm("PBEWithMD5AndDES");
        standardPBEStringEncryptor.setPassword("EWRREWRERWECCCXC");
        String name = standardPBEStringEncryptor.encrypt("root");
        String password =standardPBEStringEncryptor.encrypt("root");
        String url =standardPBEStringEncryptor.encrypt("jdbc:mysql://127.0.0.1:3306/bluecat?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai");
        System.out.println("name="+name);
        System.out.println("password="+password);
        System.out.println("url="+url);
    }
}
