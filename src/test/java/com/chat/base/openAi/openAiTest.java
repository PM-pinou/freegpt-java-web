package com.chat.base.openAi;

import com.chat.base.RunnerTest;
import com.chat.base.bean.entity.GptModelConfig;
import com.chat.base.bean.entity.UserInfo;
import com.chat.base.bean.req.UserInfoLoginReq;
import com.chat.base.bean.vo.CacheUserInfoVo;
import com.chat.base.handler.UserManager;
import com.chat.base.handler.WeightAlgorithmManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class openAiTest extends RunnerTest {

    @Autowired
    private WeightAlgorithmManager weightAlgorithmManager;

    @Autowired
    private UserManager userManager;


    @Test
    public void test(){
        CacheUserInfoVo cacheUserInfoVo = new CacheUserInfoVo();
        UserInfoLoginReq loginReq = new UserInfoLoginReq();
        loginReq.setAccount("15889198403");
        loginReq.setPassword("6ba6e2767b7a682e8d4ec2afc7cda368");
        userManager.login(loginReq,"127.0.0.1");
        UserInfo user  = new UserInfo();
        user.setId(15889198403L);
        weightAlgorithmManager.initAlgorithm(cacheUserInfoVo);
        Optional<GptModelConfig> round = weightAlgorithmManager.round(cacheUserInfoVo,"");
        if(round.isPresent()){

        }
    }
}
