package com.chat.base;

import io.github.asleepyfish.annotation.EnableChatGPT;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@EnableAsync
@MapperScan("com.chat.base.mapper.**")
public class ChatBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatBaseApplication.class, args);
    }

}
