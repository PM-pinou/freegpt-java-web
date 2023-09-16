package com.chat.base.user;


import com.chat.base.RunnerTest;
import com.chat.base.bean.req.UserInfoLoginReq;
import com.chat.base.bean.req.UserInfoRegisterReq;
import com.chat.base.controller.UserInfoController;
import com.chat.base.handler.UserLogManager;
import com.chat.base.mapper.UserInfoMapper;
import com.chat.base.utils.ResultVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class UserInfoTest extends RunnerTest {

    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    UserLogManager userLogManager;

    @Autowired
    UserInfoController userInfoController;

    @Test
    public void login() {
        UserInfoLoginReq loginReq = new UserInfoLoginReq();
        loginReq.setAccount("admin4");
        loginReq.setPassword("admin");
        ResultVO login = userInfoController.login(loginReq,null);
        assert login !=null;
    }

    @Test
    public void register(){
        UserInfoRegisterReq registerReq = new UserInfoRegisterReq();
        registerReq.setAccount("admin");
        registerReq.setPassword("admin");
        registerReq.setUsername("ai助手-test");
        ResultVO register = userInfoController.register(registerReq);
        assert register!=null;
    }

    @Test
    public void testUserLogQuery(){
//        userLogManager.queryUserLog(0,20,"blueCat",null,1,"",null,null);
    }
}
