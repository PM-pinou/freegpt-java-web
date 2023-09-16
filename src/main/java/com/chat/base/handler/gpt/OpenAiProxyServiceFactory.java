package com.chat.base.handler.gpt;

import com.chat.base.bean.vo.GptModelConfigVo;
import com.chat.base.config.ChatBaseGPTProperties;
import com.chat.base.handler.GptModelConfigManager;
import com.chat.base.service.ChatBaseOpenAiProxyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 * @author huyd
 * @date 2023/5/23 8:09 PM
 */
@Slf4j
public class OpenAiProxyServiceFactory {

    private static final Map<String,List<ChatBaseOpenAiProxyService>> serviceMap = new ConcurrentHashMap<>();

    /**
     * 获取当前模型的服务类
     *
     * @param modelId
     * @return
     */
    public static ChatBaseOpenAiProxyService createProxyService(String modelId){
        List<ChatBaseOpenAiProxyService> proxyServices = serviceMap.get(modelId);
        if(CollectionUtils.isEmpty(proxyServices)){
            return null;
        }
        return proxyServices.get(0);
    }


    public static ChatBaseOpenAiProxyService getService(String token,String proxyUrl,String model){
        ChatBaseGPTProperties properties = new ChatBaseGPTProperties();
        properties.setChatModel(model);
        properties.setSessionExpirationTime(2);
        properties.setToken(token);
        properties.setBaseUrl(proxyUrl);
        return new ChatBaseOpenAiProxyService(properties,123123123123123L);
    }


    /**
     * 移除某一个服务类
     * @param model
     * @param id 模型配置id
     */
    public static void removeModelService(String model,Long id){
        List<ChatBaseOpenAiProxyService> chatBaseOpenAiProxyServices = serviceMap.get(model);
        if(CollectionUtils.isEmpty(chatBaseOpenAiProxyServices)){
            return;
        }
        Integer index = null;
        for (int i = 0; i < chatBaseOpenAiProxyServices.size(); i++) {
            if(id.equals(chatBaseOpenAiProxyServices.get(i).getId())){
                index = i;
                break;
            }
        }
        if(index!=null){
            List<ChatBaseOpenAiProxyService> proxyServices = new ArrayList<>(chatBaseOpenAiProxyServices);
            proxyServices.remove(index);
            serviceMap.put(model,proxyServices);
        }
    }



    /**
     * 修改某一个服务类
     */
    public static void updateModelService(GptModelConfigVo gptModelConfigVo) {
        Long id = gptModelConfigVo.getId();

        Map.Entry<List<ChatBaseOpenAiProxyService>, Integer> indexEntry = serviceMap.values().stream()
                // Filter out lists that are empty or null
                .filter(Objects::nonNull)
                .filter(proxyServices -> !proxyServices.isEmpty())
                // Find the first service list and its index where a service has the given id
                .flatMap(proxyServices -> IntStream.range(0, proxyServices.size())
                        .mapToObj(i -> new AbstractMap.SimpleEntry<>(proxyServices, i))
                        .filter(entry -> id.equals(entry.getKey().get(entry.getValue()))))
                .findFirst()
                .orElse(null);

        // If a service with the given id was found, remove it and add the new one
        if (indexEntry != null) {
            indexEntry.getKey().remove((int) indexEntry.getValue());
            addModelService(gptModelConfigVo);
        }
    }


    public static void addModelService(GptModelConfigVo gptModelConfigVo){
        try {
            List<ChatBaseOpenAiProxyService> openAiProxyServiceList = serviceMap.get(gptModelConfigVo.getId().toString());
            if(CollectionUtils.isEmpty(openAiProxyServiceList)){
                openAiProxyServiceList = new ArrayList<>();
                serviceMap.put(gptModelConfigVo.getId().toString(),openAiProxyServiceList);
            }
            ChatBaseGPTProperties properties = new ChatBaseGPTProperties();
            properties.setChatModel(gptModelConfigVo.getModel());
            properties.setSessionExpirationTime(2);
            properties.setToken(gptModelConfigVo.getToken());
            properties.setBaseUrl(gptModelConfigVo.getBaseUrl());
            ChatBaseOpenAiProxyService openAiProxyService = new ChatBaseOpenAiProxyService(properties,gptModelConfigVo.getId());

            List<ChatBaseOpenAiProxyService> services = Collections.synchronizedList(openAiProxyServiceList);
            services.add(openAiProxyService);
        }catch (Exception e){
            // 在这里加上catch 防止初始化一个key有问题 从而影响其它的key了
            log.error("initGptModelConfig error gptModelConfigVo={}",gptModelConfigVo,e);
        }
    }

//
//    /**
//     * 初始化gpt模型配置
//     * @param gptModelConfigManager
//     */
//    public static void initGptModelConfig(GptModelConfigManager gptModelConfigManager){
//        List<GptModelConfigVo> gptModelConfigVos = gptModelConfigManager.getAllValidGptConfig();
//        log.info("gpt model config  init size={}",gptModelConfigVos.size());
//        for (GptModelConfigVo gptModelConfigVo : gptModelConfigVos) {
//            addModelService(gptModelConfigVo);
//        }
//    }


    /**
     * 初始化gpt模型配置
     * @param gptModelConfigManager
     */
    public static void initGptModelConfig(GptModelConfigManager gptModelConfigManager){
        List<GptModelConfigVo> gptModelConfigVos = gptModelConfigManager.getAllValidGptConfig();
        log.info("gpt model config  init size={}",gptModelConfigVos.size());
        for (GptModelConfigVo gptModelConfigVo : gptModelConfigVos) {
            try {
                List<ChatBaseOpenAiProxyService> openAiProxyServiceList = serviceMap.get(gptModelConfigVo.getId().toString());
                if(CollectionUtils.isEmpty(openAiProxyServiceList)){
                    openAiProxyServiceList = new ArrayList<>();
                    serviceMap.put(gptModelConfigVo.getId().toString(),openAiProxyServiceList);
                }
                ChatBaseGPTProperties properties = new ChatBaseGPTProperties();
                properties.setChatModel(gptModelConfigVo.getModel());
                properties.setSessionExpirationTime(2);
                properties.setToken(gptModelConfigVo.getToken());
                properties.setBaseUrl(gptModelConfigVo.getBaseUrl());
                ChatBaseOpenAiProxyService openAiProxyService = new ChatBaseOpenAiProxyService(properties,gptModelConfigVo.getId());
                openAiProxyServiceList.add(openAiProxyService);
            }catch (Exception e){
                // 在这里加上catch 防止初始化一个key有问题 从而影响其它的key了
                log.error("initGptModelConfig error gptModelConfigVo={}",gptModelConfigVo,e);
            }
        }
    }

    public static Map<String, List<ChatBaseOpenAiProxyService>> getServiceMap() {
        return serviceMap;
    }
}
