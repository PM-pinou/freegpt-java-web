package com.chat.base.job;

import com.chat.base.bean.entity.LoginUserInfo;
import com.chat.base.bean.entity.UserInfo;
import com.chat.base.bean.vo.CacheUserInfoVo;
import com.chat.base.handler.UserManager;
import com.chat.base.handler.WeightAlgorithmManager;
import com.chat.base.service.impl.LoginUserInfoServiceImpl;
import com.chat.base.utils.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Component
public class UserLoginUserJob {

    @Autowired
    private LoginUserInfoServiceImpl loginUserInfoService;

    @Autowired
    private WeightAlgorithmManager weightAlgorithmManager;

    @Autowired
    private UserManager userManager;

    @Async
    @Scheduled(cron = "0 0/10 * * * ?")
    public void buildLoginUserInfo(){
        loginUserInfoService.buildLogInfoUser();
    }

    /**
     * 重新将登录的账号重新加入缓存中
     */
    @PostConstruct
    public void buildCacheUserByLoginUser(){
        long startTime = System.currentTimeMillis();
        List<LoginUserInfo> loginUserInfos = loginUserInfoService.getBaseMapper().getAll();
        if(CollectionUtils.isNotEmpty(loginUserInfos)){
            for (LoginUserInfo loginUserInfo : loginUserInfos) {
                try {
                    long userId = Long.parseLong(loginUserInfo.getUserInfo());
                    UserInfo userInfo = userManager.queryUserInfoById(userId);
                    log.info("buildCacheUserByLoginUser userInfo={}",userInfo);
                    if(userInfo!=null){
                        CacheUserInfoVo newCacheUserInfoVo = userManager.getCacheUserInfoVoByUserByUserBean(userInfo, "127.0.0.1");
                        // 重新加载内存中的用户信息时，需要重新加载和模型的绑定关系
                        CacheUtil.put(loginUserInfo.getSessionId(),newCacheUserInfoVo);
                    }
                }catch (Exception e){
                    log.error("buildCacheUserByLoginUser user={} error ",loginUserInfo,e);
                }
            }
        }
        log.info("buildCacheUserByLoginUser end cacheSize={} cost={}",loginUserInfos.size(),System.currentTimeMillis()-startTime);
    }

}
