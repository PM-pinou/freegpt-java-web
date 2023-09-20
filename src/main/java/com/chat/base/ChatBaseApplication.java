package com.chat.base;

import com.alibaba.fastjson.JSON;
import com.chat.base.utils.SessionUser;
import com.google.gson.JsonObject;
import io.github.asleepyfish.annotation.EnableChatGPT;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.Arrays;


@Slf4j
@SpringBootApplication
@EnableScheduling
@EnableAsync
@MapperScan("com.chat.base.mapper.**")
public class ChatBaseApplication {

    @Value("${manager.accounts}")
    private String managerAccounts;


    public static void main(String[] args) {
        SpringApplication.run(ChatBaseApplication.class, args);
    }


    @PostConstruct
    public void initManagerAccount(){
        SessionUser.adminSet.addAll(Arrays.asList(managerAccounts.split(",")));
        log.info("initManagerAccount adminSet={}", JSON.toJSONString(SessionUser.adminSet));
    }
}
