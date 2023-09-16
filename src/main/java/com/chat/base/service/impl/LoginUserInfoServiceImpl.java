package com.chat.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chat.base.bean.entity.LoginUserInfo;
import com.chat.base.bean.vo.CacheUserInfoVo;
import com.chat.base.mapper.LoginUserInfoMapper;
import com.chat.base.service.ILoginUserInfoService;
import com.chat.base.utils.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>
 * 用户登录的id 服务实现类
 * </p>
 *
 * @author lixin
 * @since 2023-05-11
 */
@Slf4j
@Service
public class LoginUserInfoServiceImpl extends ServiceImpl<LoginUserInfoMapper, LoginUserInfo> implements ILoginUserInfoService {

    @Transactional
    public void buildLogInfoUser(){
        long startTime = System.currentTimeMillis();
        Boolean result = null;
        this.getBaseMapper().deleteAll();
        ConcurrentMap<String, CacheUserInfoVo> cacheUserInfo = CacheUtil.getAllCacheUserInfo();
        if(cacheUserInfo.size()>0){
            List<LoginUserInfo> loginUserInfos = new ArrayList<>();
            for (Map.Entry<String, CacheUserInfoVo> entry : cacheUserInfo.entrySet()) {
                LoginUserInfo loginUserInfo = new LoginUserInfo();
                loginUserInfo.setSessionId(entry.getKey());
                loginUserInfo.setUserInfo(entry.getKey());
                loginUserInfos.add(loginUserInfo);
            }
            result = this.saveBatch(loginUserInfos);
        }
        log.info("buildLoginUserInfo end cacheSize={} ,result={} cost={}",cacheUserInfo.size(),result,System.currentTimeMillis()-startTime);
    }


    public  List<LoginUserInfo> queryOnlineUserInfo(){
        return this.baseMapper.getAll();
    }

}
