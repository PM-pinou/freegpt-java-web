package com.chat.base.handler;

import com.chat.base.bean.entity.GptModelConfig;
import com.chat.base.bean.vo.CacheGptApiTokenVo;
import com.chat.base.bean.vo.CacheGptModelConfigVo;
import com.chat.base.bean.vo.CacheUserInfoVo;
import com.chat.base.utils.CacheUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import static com.google.common.math.IntMath.gcd;

@Component
@Slf4j
public class WeightAlgorithmManager {

    private final Cache<Long,  CacheUserInfoVo> configCache =
            CacheBuilder.newBuilder().maximumSize(2000).expireAfterWrite(1, TimeUnit.MINUTES).build();


    @Autowired
    private GptApiTokenConfigManager gptApiTokenConfigManager;

    @Autowired
    private ChannelModelConfigManager channelModelConfigManager;

    @Autowired
    private GptModelConfigManager gptModelConfigManager;

    public void initAllOnlineUserAlgorithm(){
        ConcurrentMap<String, CacheUserInfoVo> cacheUserInfo = CacheUtil.getAllCacheUserInfo();
        for (Map.Entry<String, CacheUserInfoVo> stringStringEntry : cacheUserInfo.entrySet()) {
            CacheUserInfoVo cacheInfo = stringStringEntry.getValue();
            initAlgorithm(cacheInfo);
        }
    }

    public CacheUserInfoVo initAlgorithm(CacheUserInfoVo cacheUserInfoVo){
        if (Objects.isNull(cacheUserInfoVo)){
            return null;
        }

        // 1、查数据库获取token
        CacheGptApiTokenVo entity = gptApiTokenConfigManager.queryGptApiTokenByUserId(cacheUserInfoVo.getId());
        if(entity!=null){
            // 这里需要保证 缓存中的GptApiTokenVo对象唯一
            synchronized (cacheUserInfoVo.getClass()){
                if(cacheUserInfoVo.getGptApiTokenVo()==null){
                    cacheUserInfoVo.setGptApiTokenVo(entity);
                }else{
                    CacheGptApiTokenVo gptApiTokenVo = cacheUserInfoVo.getGptApiTokenVo();
                    gptApiTokenVo.setBalance(entity.getBalance());
                    gptApiTokenVo.setVisitNumber(entity.getVisitNumber());
                    gptApiTokenVo.setChannelIds(entity.getChannelIds());
                }
            }
        }

        // 2、 根据token -> channel -> model
        List<String> channelIds = entity.getChannelIds();
        if(CollectionUtils.isNotEmpty(channelIds)){
            List<String> modelIds = channelModelConfigManager.queryModelIdsByChannelIds(channelIds);
            if (CollectionUtils.isNotEmpty(modelIds)){
                List<GptModelConfig> gptModelConfigs = gptModelConfigManager.queryGptModelConfigByModelIds(modelIds);
                Map<String, List<GptModelConfig>> stringListMap = ModelConfigMap(gptModelConfigs);
                Map<String, CacheGptModelConfigVo> modelConfigMap = new HashMap<>();
                for (Map.Entry<String, List<GptModelConfig>> stringListEntry : stringListMap.entrySet()) {
                    if(CollectionUtils.isNotEmpty(stringListEntry.getValue())){
                        List<GptModelConfig> modelConfigs = stringListEntry.getValue();
                        OptionalInt maxWeight = modelConfigs.stream()
                                .mapToInt(GptModelConfig::getWeight)
                                .max();
                        CacheGptModelConfigVo cacheGptModelConfigVo = new CacheGptModelConfigVo();
                        cacheGptModelConfigVo.setGptModelConfigs(modelConfigs);
                        cacheGptModelConfigVo.setTotalModel(modelConfigs.size());
                        cacheGptModelConfigVo.setCurrentIndex(modelConfigs.size()-1);
                        cacheGptModelConfigVo.setMaxWeight(maxWeight.getAsInt());
                        cacheGptModelConfigVo.setGcdWeight(serverGcd(modelConfigs));
                        cacheGptModelConfigVo.setCurrentWeight(0);
                        modelConfigMap.put(stringListEntry.getKey(),cacheGptModelConfigVo);
                    }
                }
                cacheUserInfoVo.setGptModelConfigsMap(modelConfigMap);
            }
        }
        return cacheUserInfoVo;
    }



    private Map<String, List<GptModelConfig>> ModelConfigMap(List<GptModelConfig> modelConfigs){
        Map<String, List<GptModelConfig>> modelMap = new HashMap<>();

        // 遍历 modelConfigs，根据 model 字段将对象添加到 Map 中
        for (GptModelConfig config : modelConfigs) {
            String model = config.getModel();

            // 如果 Map 中已经存在该 model 的键，就将当前对象添加到对应的列表中
            // 否则，创建一个新的列表，并将当前对象加入其中，然后将该列表作为值存入 Map
            if (modelMap.containsKey(model)) {
                modelMap.get(model).add(config);
            } else {
                List<GptModelConfig> newList = new ArrayList<>();
                newList.add(config);
                modelMap.put(model, newList);
            }
        }
        return modelMap;
    }


    /**
     * 加权轮训算法
     */
    public  Optional<GptModelConfig> round(CacheUserInfoVo cacheUserInfoVo,String model) {
        try {
        CacheGptModelConfigVo cacheGptModelConfigVo = cacheUserInfoVo.getGptModelConfigsMap().get(model);
        if(Objects.isNull(cacheGptModelConfigVo)){
            return Optional.empty();
        }
        List<GptModelConfig> gptModelConfigs = cacheGptModelConfigVo.getGptModelConfigs();
        if(CollectionUtils.isNotEmpty(gptModelConfigs)){
            for (int i = 0; i < cacheGptModelConfigVo.getTotalModel(); i++) {
                cacheGptModelConfigVo.setCurrentIndex((cacheGptModelConfigVo.getCurrentIndex() + 1) % cacheGptModelConfigVo.getTotalModel());
                if (cacheGptModelConfigVo.getCurrentIndex() == 0) {
                    cacheGptModelConfigVo.setCurrentWeight(cacheGptModelConfigVo.getCurrentWeight() - cacheGptModelConfigVo.getGcdWeight());
                    if (cacheGptModelConfigVo.getCurrentWeight() <= 0) {
                        cacheGptModelConfigVo.setCurrentWeight(cacheGptModelConfigVo.getMaxWeight());
                        if(cacheGptModelConfigVo.getCurrentWeight() == 0) {
                            log.info("currentWeight==0");
                            break;
                        }
                    }
                }
                if(cacheGptModelConfigVo.getCurrentIndex() <= gptModelConfigs.size() && gptModelConfigs.get(cacheGptModelConfigVo.getCurrentIndex()).getWeight() >= cacheGptModelConfigVo.getCurrentWeight()) {
                    GptModelConfig gptModelConfig = gptModelConfigs.get(cacheGptModelConfigVo.getCurrentIndex());
                    cacheUserInfoVo.getGptModelConfigsMap().put(model,cacheGptModelConfigVo);
                    return Optional.of(gptModelConfig);
                }
            }
        }
        } catch (Exception e) {
            log.error("round error={}", e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * 返回所有模型的权重的最大公约数
     *
     * @return
     */
    private static int serverGcd(List<GptModelConfig> gptModelConfigs) {
        int comDivisor = 0;
        for (int i = 0; i < gptModelConfigs.size() - 1; i++) {
            if (comDivisor == 0) {
                //我只是图个方便用的map 大家可以封装一个类
                comDivisor = gcd(gptModelConfigs.get(i).getWeight(), gptModelConfigs.get(i+1).getWeight());
            } else {
                comDivisor = gcd(comDivisor, gptModelConfigs.get(i+1).getWeight());
            }
        }
        return comDivisor;
    }
}
