package com.chat.base.handler.gpt;

import com.chat.base.bean.vo.GptModelConfigVo;
import com.chat.base.config.ChatBaseGPTProperties;
import com.chat.base.handler.GptModelConfigManager;
import com.chat.base.service.ChatBaseOpenAiProxyService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huyd
 * @date 2023/5/23 8:09 PM
 */
@Slf4j
public class OpenAiProxyServiceFactory {

    private static final Map<String, ChatBaseOpenAiProxyService> serviceMap = new ConcurrentHashMap<>();

    /**
     * 获取当前模型的服务类
     *
     * @param modelId
     * @return
     */
    public static ChatBaseOpenAiProxyService createProxyService(String modelId){
        ChatBaseOpenAiProxyService openAiProxyService = serviceMap.get(modelId);
        if(openAiProxyService==null){
            return null;
        }
        return openAiProxyService;
    }


    public static ChatBaseOpenAiProxyService getService(String token, String proxyUrl, String model){
        ChatBaseGPTProperties properties = new ChatBaseGPTProperties();
        properties.setChatModel(model);
        properties.setSessionExpirationTime(2);
        properties.setToken(token);
        properties.setBaseUrl(proxyUrl);
        return new ChatBaseOpenAiProxyService(properties,123123123123123L);
    }


    /**
     * 移除某一个服务类
     * @param id 模型配置id
     */
    public static void removeModelService(Long id){
        ChatBaseOpenAiProxyService chatBaseOpenAiProxyServices = serviceMap.get(id.toString());
        if(chatBaseOpenAiProxyServices==null){
            return;
        }
        serviceMap.remove(id.toString());
    }



    /**
     * 修改某一个服务类
     */
    public static void updateModelService(GptModelConfigVo gptModelConfigVo) {
        Long id = gptModelConfigVo.getId();
        serviceMap.remove(id.toString());
        addModelService(gptModelConfigVo);
    }


    public static void addModelService(GptModelConfigVo gptModelConfigVo){
        try {
            ChatBaseGPTProperties properties = new ChatBaseGPTProperties();
            properties.setChatModel(gptModelConfigVo.getModel());
            properties.setSessionExpirationTime(2);
            properties.setToken(gptModelConfigVo.getToken());
            properties.setBaseUrl(gptModelConfigVo.getBaseUrl());
            ChatBaseOpenAiProxyService openAiProxyService = new ChatBaseOpenAiProxyService(properties,gptModelConfigVo.getId());
            serviceMap.put(gptModelConfigVo.getId().toString(),openAiProxyService);
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
                ChatBaseGPTProperties properties = new ChatBaseGPTProperties();
                properties.setChatModel(gptModelConfigVo.getModel());
                properties.setSessionExpirationTime(2);
                properties.setToken(gptModelConfigVo.getToken());
                properties.setBaseUrl(gptModelConfigVo.getBaseUrl());
                ChatBaseOpenAiProxyService openAiProxyService = new ChatBaseOpenAiProxyService(properties,gptModelConfigVo.getId());
                serviceMap.put(gptModelConfigVo.getId().toString(),openAiProxyService);
            }catch (Exception e){
                // 在这里加上catch 防止初始化一个key有问题 从而影响其它的key了
                log.error("initGptModelConfig error gptModelConfigVo={}",gptModelConfigVo,e);
            }
        }
    }

    public static Map<String, ChatBaseOpenAiProxyService> getServiceMap() {
        return serviceMap;
    }
}
