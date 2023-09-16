package com.chat.base.job;

import com.chat.base.bean.entity.GptApiTokenConfig;
import com.chat.base.bean.vo.CacheGptApiTokenVo;
import com.chat.base.bean.vo.CacheUserInfoVo;
import com.chat.base.handler.billing.impl.ModelBillingByBalanceImpl;
import com.chat.base.service.impl.GptApiTokenConfigServiceImpl;
import com.chat.base.utils.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 更新用户访问规则
 */
@Slf4j
@Component
public class UserApiTokenConfigJob {

    @Resource
    private GptApiTokenConfigServiceImpl tokenConfigService;

    @Autowired
    private ModelBillingByBalanceImpl modelBillingByBalance;

    /**
     * 定时更新缓存中的 访问规则类
     */
    @Async
    @Scheduled(cron = "0 0/1 * * * ?")
    public void updateUserApiTokenConfig(){
        long startTime = System.currentTimeMillis();
        log.info("updateUserApiTokenConfig 开始执行");
        Collection<CacheUserInfoVo> cacheUserInfoVos = CacheUtil.getUserInfo();

        List<CacheGptApiTokenVo> apiTokenVos = new ArrayList<>();
        for (CacheUserInfoVo cacheUserInfoVo : cacheUserInfoVos) {
            CacheGptApiTokenVo gptApiTokenVo = cacheUserInfoVo.getGptApiTokenVo();
            if(gptApiTokenVo!=null){
                // todo 一次性更新所有登录用户的模型配置 这样到导致不需要更新的也会进行更新 待优化 等量大这里可能会有问题
                apiTokenVos.add(gptApiTokenVo);
            }
        }
        if(CollectionUtils.isEmpty(apiTokenVos)){
            log.info("updateUserApiTokenConfig not have update access rule");
            return;
        }

        List<GptApiTokenConfig> configs = apiTokenVos.stream().map(e -> {
            CacheUserInfoVo cacheUserInfoVo = CacheUtil.getIfPresent(String.valueOf(e.getUserId()));
            synchronized (cacheUserInfoVo.getClass()){
                GptApiTokenConfig config = new GptApiTokenConfig();
                config.setId(e.getId());
                config.setUserId(e.getUserId());
                config.setVisitNumber(e.getVisitNumber());
                config.setBalance(e.getBalance());
                return config;
            }
        }).collect(Collectors.toList());

        List<GptApiTokenConfig> needUpdateTokenConfig = getNeedUpdateTokenConfig(configs);
        boolean result = tokenConfigService.batchUpdate(needUpdateTokenConfig);
        log.info("updateUserApiTokenConfig 执行结束 result={} cost={}",result,System.currentTimeMillis()-startTime);
    }

    /**
     * 获取需要更新的规则类
     * @param apiTokenConfigs
     * @return
     */
    private List<GptApiTokenConfig> getNeedUpdateTokenConfig(List<GptApiTokenConfig> apiTokenConfigs){

        //需要更新的访问配置
        List<GptApiTokenConfig> needUpdateConfigList = new ArrayList<>();

        Map<Long, GptApiTokenConfig> cacheTokenConfigMap = apiTokenConfigs.stream()
                .collect(Collectors.toMap(GptApiTokenConfig::getId, Function.identity()));

        Set<Long> ids = cacheTokenConfigMap.keySet();
        List<GptApiTokenConfig> entityConfigs = tokenConfigService.getGptApiTokenConfigByIds(ids);
        for (GptApiTokenConfig entity : entityConfigs) {

            try {
                //缓存的token配置
                GptApiTokenConfig cacheConfig = cacheTokenConfigMap.get(entity.getId());
                if(cacheConfig.getBalance().equals(entity.getBalance()) && cacheConfig.getVisitNumber().equals(entity.getVisitNumber())){
                    continue;
                }
                log.info("getNeedUpdateTokenConfig cacheInfo={},entityInfo={}",cacheConfig,entity);
                // 如果实体和缓存的调用次数不一致 则是需要更新到数据库
                entity.setBalance(cacheConfig.getBalance());
                entity.setVisitNumber(cacheConfig.getVisitNumber());
                needUpdateConfigList.add(entity);
            }catch (Exception e){
                log.error("getNeedUpdateTokenConfig error entity={}",entity,e);
            }
        }
        return needUpdateConfigList;
    }
}
